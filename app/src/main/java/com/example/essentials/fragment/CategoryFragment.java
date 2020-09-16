package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.CategoryPresentationBean;
import com.example.essentials.adapter.CategoryRecyclerViewAdapter;
import com.example.essentials.domain.Category;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CategoryViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryFragment extends Fragment implements CategoryRecyclerViewAdapter.ListItemClickListener{
    CategoryViewModel categoryViewModel;
    CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    View rootView;
    List<Category> categories = new ArrayList<>();
    List<CategoryPresentationBean> categoryPresentationBeans = new ArrayList<>();
    RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        categoryViewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);
        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        categoryViewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);

        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_actionbar, null);

        if (actionBar != null) {
            // enable the customized view and disable title
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(view);
         //  actionBar.setTitle(getResources().getString(R.string.categories));
            actionBar.setDisplayShowTitleEnabled(false);


            // remove Burger Icon
            toolbar.setNavigationIcon(null);
        }
        getCategories();

        TextView titleView = view.findViewById(R.id.actionbar_view);
        titleView.setText(getResources().getString(R.string.categories));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        getActivity(), drawer, toolbar, R.string.drawer_open,
                        R.string.drawer_close);
                // All that to re-synchronize the Drawer State
                toggle.syncState();
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }

    private void getCategories() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), objCategories -> {
            categories = objCategories;
            if (!categories.isEmpty()) {
                categoryPresentationBeans = EssentialsUtils.getCategoryPresentationBean(categories);
                setData(categoryPresentationBeans);
            }
        });
    }

    private void setData(List<CategoryPresentationBean> categoryPresentationBeans) {
        categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getActivity(), categoryPresentationBeans, this);
        recyclerView = rootView.findViewById(R.id.rv_category);
        recyclerView.setAdapter(categoryRecyclerViewAdapter);


        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);

    }

    @Override
    public void onListItemClick(CategoryPresentationBean categoryPresentationBean) {
       // ProductFragmentDirections.NavigateToProductDetailFragment action = ProductFragmentDirections.navigateToProductDetailFragment(productPresentationBean);
        CategoryFragmentDirections.ActionNavBottomCategoryToNavBottomHome action = CategoryFragmentDirections.actionNavBottomCategoryToNavBottomHome();
        action.setCategoryId(categoryPresentationBean.getId());
        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.CATEGORY_ID, categoryPresentationBean.getId());
       // Navigation.findNavController(rootView).navigate(action);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        if(recyclerView!=null && categoryPresentationBeans!=null) {
            Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            // putting recyclerview position
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
            // putting recyclerview items
            savedInstanceState.putParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(categoryPresentationBeans));
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID);
        // getting recyclerview items
        if(recyclerView!=null && categoryPresentationBeans!=null) {
            categoryPresentationBeans = savedInstanceState.getParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID);
            // Restoring adapter items
            setData(categoryPresentationBeans);
            // Restoring recycler view position
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }
}

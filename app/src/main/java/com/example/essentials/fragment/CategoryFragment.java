package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.CategoryRecyclerViewAdapter;
import com.example.essentials.domain.Category;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CategoryViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryRecyclerViewAdapter.ListItemClickListener{

    String TAG = "CategoryFragment";
    CategoryViewModel categoryViewModel;
    CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    View rootView;
    List<Category> categories = new ArrayList<>();

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
        categoryViewModel.getAllCategories().observe(this, objCategories -> {
            categories = objCategories;
            Log.d("categories", String.valueOf(categories.size()));
            if (!categories.isEmpty()) {
                setData(categories);
            }
        });
    }

    private void setData(List<Category> categories) {
        categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getActivity(), EssentialsUtils.getCategoryPresentationBean(categories), this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_category);
        recyclerView.setAdapter(categoryRecyclerViewAdapter);


        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
          if(categories !=null && categories.size()>0) {
              logAnalyticsEvent(getActivity().getApplicationContext(), EssentialsUtils.getCategoryPresentationBean(categories));
          }
    }

    public static void logAnalyticsEvent(Context context, List<CategoryPresentationBean> categoryPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (CategoryPresentationBean productPresentationBean : categoryPresentationBeans) {
            sb.append(productPresentationBean.getName());
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.CATEGORY_PRESENTATION_BEAN);
        bundle.putString(ApplicationConstants.ITEMS, sb.substring(0, sb.lastIndexOf(",")));
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    @Override
    public void onListItemClick(CategoryPresentationBean categoryPresentationBean) {
        Log.d(TAG,"ANandhi"+categoryPresentationBean.getId());
       // ProductFragmentDirections.NavigateToProductDetailFragment action = ProductFragmentDirections.navigateToProductDetailFragment(productPresentationBean);
        CategoryFragmentDirections.ActionNavBottomCategoryToNavBottomHome action = CategoryFragmentDirections.actionNavBottomCategoryToNavBottomHome();
        action.setCategoryId(categoryPresentationBean.getId());
       // Navigation.findNavController(rootView).navigate(action);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}

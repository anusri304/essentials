package com.example.essentials.fragment;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.CategoryPresentationBean;
import com.example.essentials.adapter.CategoryRecyclerViewAdapter;
import com.example.essentials.adapter.WishlistRecyclerViewAdapter;
import com.example.essentials.domain.Category;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.CategoryViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;

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
        getCategories();
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

//        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onListItemClick(CategoryPresentationBean categoryPresentationBean) {

    }
}

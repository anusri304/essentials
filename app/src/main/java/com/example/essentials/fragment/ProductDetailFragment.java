package com.example.essentials.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;

public class ProductDetailFragment extends Fragment {

    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProductDetailFragmentArgs productDetailFragmentArgs = ProductDetailFragmentArgs.fromBundle(getArguments());
        rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ProductPresentationBean productPresentationBean = productDetailFragmentArgs.getProductPresentationBean();
        return rootView;
    }
}

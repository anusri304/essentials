package com.example.essentials.fragment;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.CartRecyclerViewAdapter;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartFragment extends Fragment  {
    View rootView;
    CartViewModel cartViewModel;
    ProductViewModel productViewModel;
    List<Product> products = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    CartRecyclerViewAdapter cartRecyclerViewAdapter;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        //tODO: Remove the below code if not used
//
//        final Observer<Integer> nameObserver = new Observer<Integer>() {
//            @Override
//            public void onChanged(@Nullable final Integer newQuantity) {
//                // Update the UI, in this case, a TextView.
//                cartViewModel = new ViewModelProvider(CartFragment.this, factory).get(CartViewModel.class);
//                int totalQuantity = cartViewModel.getAllCartItems().getValue().stream().mapToInt(cartItem -> cartItem.getQuantity()).sum();
//                Log.d("Anandhi..",String.valueOf(totalQuantity));
//       //drawBadge(totalQuantity);
//            }
//        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        //cartViewModel.getQuantity().observe(this,nameObserver);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        getAllProducts();
        return rootView;
    }


    private void getAllProducts() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;
            Log.d("products", String.valueOf(products.size()));
            if (!products.isEmpty()) {
                getCartItems();
            }

        });
    }

    private void getCartItems() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            if (!cartItems.isEmpty()) {
                setData(cartItems);
                int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
                if(totalQuantity>0) {
                    drawBadge(totalQuantity);
                }

            }
        });

    }

    private void drawBadge(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(number);
    }

    private void setData(List<Cart> cartItems) {
        List<ProductPresentationBean> filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                cartItems.stream().map(cart -> cart.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        setProductData(filteredProductPresentationBeans);

    }
    private void setProductData(List<ProductPresentationBean> productPresentationBeans) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        cartRecyclerViewAdapter = new CartRecyclerViewAdapter(getActivity(), productPresentationBeans,cartViewModel,bottomNavigationView);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_cart);
        recyclerView.setAdapter(cartRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }



}

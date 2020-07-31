package com.example.essentials.fragment;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.adapter.WishlistRecyclerViewAdapter;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    String TAG = "WishlistFragment";
    WishlistViewModel wishlistViewModel;
    ProductViewModel productViewModel;
    Wishlist wishlist;
    List<Wishlist> wishlists = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    WishlistRecyclerViewAdapter wishlistRecyclerViewAdapter;
    View rootView;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

       // getWishlistProducts();
        getAllProducts();

        rootView =  inflater.inflate(R.layout.fragment_wishlist, container, false);
        return rootView;
    }

    private void getWishlistProducts() {
        Log.d("Anandhi...", "observeChanges");
        wishlistViewModel.getAllWishlist().observe(this, objWishlist -> {
            wishlists = objWishlist;

            if (!wishlists.isEmpty()) {
                setData(wishlists);
            }
        });
    }

    private void   getAllProducts(){
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;

            setProductData(EssentialsUtils.getProductPresentationBeans(products));
        });
    }

    private void setData(List<Wishlist> wishlists) {
        for(Wishlist wishlist:wishlists){
            int productId = wishlist.getProductId();
        }
    }

    private void setProductData(List<ProductPresentationBean> productPresentationBeans) {
        Log.d("aNANDHI",String.valueOf(productPresentationBeans.size()));
        wishlistRecyclerViewAdapter = new WishlistRecyclerViewAdapter(getActivity(), productPresentationBeans);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_wishlist);
        recyclerView.setAdapter(wishlistRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }
}

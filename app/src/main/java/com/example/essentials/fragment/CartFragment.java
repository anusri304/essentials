package com.example.essentials.fragment;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.ProductActivity;
import com.example.essentials.activity.ProductDetailActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.CartRecyclerViewAdapter;
import com.example.essentials.adapter.WishlistRecyclerViewAdapter;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.CartService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.WishlistTransportBean;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartFragment extends Fragment implements CartRecyclerViewAdapter.ListItemClickListener {
    View rootView;
    CartViewModel cartViewModel;
    ProductViewModel productViewModel;
    List<Product> products = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    private static Retrofit retrofit = null;
    WishlistViewModel wishlistViewModel;
    CartRecyclerViewAdapter cartRecyclerViewAdapter;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        view = getActivity().findViewById(android.R.id.content);

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
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
        getAllProducts();
        return rootView;
    }


    private void getAllProducts() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;
            Log.d("products", String.valueOf(products.size()));
            getCartItems();

        });
    }

    private void getCartItems() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            setData(cartItems);
            int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
                drawBadge(totalQuantity);
        });

    }

    private void drawBadge(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        if(number>0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        }
        else {
            badgeDrawable.setVisible(false);
        }
    }

    private void setData(List<Cart> cartItems) {
        List<ProductPresentationBean> filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                cartItems.stream().map(cart -> cart.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        setProductData(filteredProductPresentationBeans);

    }

    private void setProductData(List<ProductPresentationBean> productPresentationBeans) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        cartRecyclerViewAdapter = new CartRecyclerViewAdapter(getActivity(), productPresentationBeans, cartViewModel, this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_cart);
        recyclerView.setAdapter(cartRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }


    @Override
    public void onListItemClick(ProductPresentationBean productPresentationBean) {
        //Remove the product from cart and add to wishlist
        callCartEndPoint(productPresentationBean);

    }

    private void callCartEndPoint(ProductPresentationBean productPresentationBean) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi callCartEndPoint product id", String.valueOf(productPresentationBean.getId()));
        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        CartService cartService = getRetrofit().create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productPresentationBean.getId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.removeFromCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                Log.d("Anandhi cart", cartTransportBean.getMessage());
                deleteCartItemsFromDB(userId, productPresentationBean.getId());
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {

            }
        });
    }

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
// The App will not crash for malformed JSON.
        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private void deleteCartItemsFromDB(int userId, int productId) {
        Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, productId);
        if (cart != null) {
            cartViewModel.deleteCartItems(cart);
        }
        //  EssentialsUtils.showMessage(coordinatorLayout,ApplicationConstants.CART_SUCCESS_MESSAGE);
        callWishListEndpoint(userId, productId);

    }

    private void callWishListEndpoint(int userId, int productId) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi callWishListEndpoint product id", String.valueOf(productId));
        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        WishlistService wishlistService = getRetrofit().create(WishlistService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productId))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<WishlistTransportBean> call = wishlistService.addToWishlist(apiToken, requestBody);

        call.enqueue(new Callback<WishlistTransportBean>() {
            @Override
            public void onResponse(Call<WishlistTransportBean> call, Response<WishlistTransportBean> response) {
                WishlistTransportBean wishlistTransportBean = response.body();
                Log.d("Anandhi total", wishlistTransportBean.getTotal());
                saveWishListToDB(userId, productId);

            }

            @Override
            public void onFailure(Call<WishlistTransportBean> call, Throwable throwable) {

            }
        });
    }

    private void saveWishListToDB(int userId, int productId) {
        Wishlist wishlist = wishlistViewModel.getWishlistForUserAndProduct(userId, productId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist.setProductId(productId);
            wishlistViewModel.insertWishlist(wishlist);
        }
        showSnackBar();
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(view, ApplicationConstants.CART_SUCCESS_MOVE_MESSAGE, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


}
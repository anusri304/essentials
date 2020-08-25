package com.example.essentials.fragment;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.WishlistRecyclerViewAdapter;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.CartService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.WishlistTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WishlistFragment extends Fragment implements WishlistRecyclerViewAdapter.ListItemClickListener {
    String TAG = "WishlistFragment";
    WishlistViewModel wishlistViewModel;
    CartViewModel cartViewModel;
    ProductViewModel productViewModel;
    Wishlist wishlist;
    List<Wishlist> wishlists = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    WishlistRecyclerViewAdapter wishlistRecyclerViewAdapter;
    View rootView;
    private static Retrofit retrofit = null;
    CoordinatorLayout coordinatorLayout;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().findViewById(android.R.id.content);

        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        getAllProducts();
        rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        observeCartChanges();
        return rootView;
    }

    private void observeCartChanges() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
            drawBadge(totalQuantity);

        });
    }


    private void drawBadge(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }


    private void getWishlistProducts() {
        wishlistViewModel.getAllWishlist().observe(this, objWishlist -> {
            wishlists = objWishlist;
            Log.d("wishlist", String.valueOf(wishlists.size()));
            if (!wishlists.isEmpty()) {
                setData(wishlists);
            }
            else {
                TextView titleTextView = rootView.findViewById(R.id.title_wishlist);
                titleTextView.setVisibility(View.INVISIBLE);
                EssentialsUtils.showMessageAlertDialog(getActivity(),ApplicationConstants.NO_ITEMS,ApplicationConstants.NO_ITEMS_WISH_LIST);

            }
        });
    }

    private void getAllProducts() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;
            Log.d("products", String.valueOf(products.size()));
            if (!products.isEmpty()) {
                getWishlistProducts();
            }

        });
    }

    private void setData(List<Wishlist> wishlists) {
        List<ProductPresentationBean> filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                wishlists.stream().map(wishList -> wishList.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        Log.d("filteredData", String.valueOf(filteredProductPresentationBeans.size()));
        setProductData(filteredProductPresentationBeans);

    }

    private void setProductData(List<ProductPresentationBean> productPresentationBeans) {
        wishlistRecyclerViewAdapter = new WishlistRecyclerViewAdapter(getActivity(), productPresentationBeans, this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_wishlist);
        recyclerView.setAdapter(wishlistRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onListItemClick(ProductPresentationBean productPresentationBean) {
        // ProductPresentationBean productPresentationBean = EssentialsUtils.getProductPresentationBeans(products).get(clickedItemIndex);
callRemoveWishListEndpoint(productPresentationBean);
callCartEndPoint(productPresentationBean);

    }


    private void callRemoveWishListEndpoint(ProductPresentationBean productPresentationBean) {
        SharedPreferences pref = getActivity().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi callWishListEndpoint product id", String.valueOf(productPresentationBean.getId()));
        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        WishlistService wishlistService = APIUtils.getRetrofit().create(WishlistService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productPresentationBean.getId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<WishlistTransportBean> call = wishlistService.removeFromWishlist(apiToken, requestBody);

        call.enqueue(new Callback<WishlistTransportBean>() {
            @Override
            public void onResponse(Call<WishlistTransportBean> call, Response<WishlistTransportBean> response) {
                WishlistTransportBean wishlistTransportBean = response.body();
               removeWishlistFromDB(userId, productPresentationBean.getId());

            }

            @Override
            public void onFailure(Call<WishlistTransportBean> call, Throwable throwable) {

            }
        });
    }

    private void removeWishlistFromDB(int userId, int productId) {
        Wishlist wishlist = wishlistViewModel.getWishlistForUserAndProduct(userId, productId);
        if (wishlist != null) {
            wishlistViewModel.deleteWishlistItems(wishlist);
        }
    }
    private void callCartEndPoint(ProductPresentationBean productPresentationBean) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi callCartEndPoint product id", String.valueOf(productPresentationBean.getId()));
        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        CartService cartService = APIUtils.getRetrofit().create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productPresentationBean.getId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.addToCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                if(response.isSuccessful()) {
                    saveCartItemsToDB(userId, productPresentationBean.getId());
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {

            }
        });
    }

    private void saveCartItemsToDB(int userId, int productId) {
        Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, productId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(cart.getQuantity() + 1);
            cartViewModel.insertCartItems(cart);
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cartViewModel.updateCartItems(cart);
        }
        //  EssentialsUtils.showMessage(coordinatorLayout,ApplicationConstants.CART_SUCCESS_MESSAGE);
        showSuccessSnackBar();
    }

    private void showSuccessSnackBar() {
        Snackbar snackbar = Snackbar
                .make(view, ApplicationConstants.CART_SUCCESS_MOVE_MESSAGE, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(WishlistFragmentDirections.actionNavBottomWishlistToNavBottomCart());
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(view, ApplicationConstants.NO_ITEMS_WISH_LIST, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}

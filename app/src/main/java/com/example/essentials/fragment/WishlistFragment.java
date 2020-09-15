package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.WishlistRecyclerViewAdapter;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.CartService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.WishlistTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.RetrofitUtils;
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
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WishlistFragment extends Fragment implements WishlistRecyclerViewAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    WishlistViewModel wishlistViewModel;
    CartViewModel cartViewModel;
    ProductViewModel productViewModel;
    Wishlist wishlist;
    List<Wishlist> wishlists = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    static List<Product> products = new ArrayList<>();
    WishlistRecyclerViewAdapter wishlistRecyclerViewAdapter;
    View rootView;
    private static Retrofit retrofit = null;
    CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeContainer;
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

        getWishlistProducts();

        //  observeWishlistChanges();


        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

        TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
        titleView.setText(getResources().getString(R.string.your_wish_list));

        if (actionBar != null) {
            // enable the customized view and disable title
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarView);
            //  actionBar.setTitle(getResources().getString(R.string.categories));
            actionBar.setDisplayShowTitleEnabled(false);


            // remove Burger Icon
            toolbar.setNavigationIcon(null);
        }
        actionBarView.setOnClickListener(new View.OnClickListener() {
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


        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(this);
        return rootView;
    }

    private void observeCartChanges() {
        cartViewModel.getAllCartItems().observe(getViewLifecycleOwner(), objCart -> {
            cartItems = objCart;
            int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
            drawBadgeForCart(totalQuantity);

        });
    }


    private void drawBadgeForCart(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }

    private void drawBadgeForWishlist(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_wishlist);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }


    private void getWishlistProducts() {
        wishlistViewModel.getAllWishlist().observe(getViewLifecycleOwner(), objWishlist -> {
            wishlists = objWishlist;
            if (wishlists.isEmpty()) {
                EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_ITEMS, ApplicationConstants.NO_ITEMS_WISH_LIST);
            }
            setData(wishlists);
            drawBadgeForWishlist(wishlists.size());
        });

    }

    private void getWishlistProductsForCustomer() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CustomerWishTransportBean.class, new AnnotatedDeserializer<CustomerWishTransportBean>())
                .setLenient().create();
        WishlistService wishlistService = RetrofitUtils.getRetrofit(gson).create(WishlistService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerWishListTransportBean> call = wishlistService.getWishListProductsForCustomer(String.valueOf(userId), apiToken);
        //Save Products to cart
        call.enqueue(new Callback<CustomerWishListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerWishListTransportBean> call, Response<CustomerWishListTransportBean> response) {
                if (response.isSuccessful()) {
                    CustomerWishListTransportBean customerWishListTransportBeans = response.body();
                    if (customerWishListTransportBeans != null && customerWishListTransportBeans.getProducts().size() > 0) {
                        for (CustomerWishTransportBean customerWishlistTransportBean : customerWishListTransportBeans.getProducts()) {
                            if (customerWishlistTransportBean.getProductId() != null && !customerWishlistTransportBean.getProductId().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                Wishlist wishlist = wishlistViewModel.getWishlistForUserAndProduct(userId, Integer.parseInt(customerWishlistTransportBean.getProductId()));
                                if (wishlist == null) {
                                    wishlist = new Wishlist();
                                    wishlist.setProductId(Integer.valueOf(customerWishlistTransportBean.getProductId()));
                                    wishlist.setUserId(userId);
                                    wishlistViewModel.insertWishlist(wishlist);
                                } else {
                                    wishlist.setProductId(Integer.valueOf(customerWishlistTransportBean.getProductId()));
                                    wishlist.setUserId(userId);
                                    wishlistViewModel.updateWishlist(wishlist);
                                }
                            } else {
                                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                            }
                        }
                    } else {
                        APIUtils.getFirebaseCrashlytics().log(WishlistFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerWishListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(WishlistFragment.class.getName().concat(" ").concat(throwable.getMessage()));
            }
        });
    }

    private void getAllProducts() {
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), objProducts -> {
            products = objProducts;

        });
    }

    private void setData(List<Wishlist> wishlists) {
        List<ProductPresentationBean> filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                wishlists.stream().map(wishList -> wishList.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        setProductData(filteredProductPresentationBeans);

        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }

    }

    private void setProductData(List<ProductPresentationBean> productPresentationBeans) {
        wishlistRecyclerViewAdapter = new WishlistRecyclerViewAdapter(getActivity(), productPresentationBeans, this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_wishlist);
        recyclerView.setAdapter(wishlistRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
        if (productPresentationBeans != null && productPresentationBeans.size() > 0) {
            APIUtils.logViewItemsAnalyticsEvent(getActivity().getApplicationContext(), productPresentationBeans);
        }
    }

    @Override
    public void onListItemClick(ProductPresentationBean productPresentationBean) {
        // ProductPresentationBean productPresentationBean = EssentialsUtils.getProductPresentationBeans(products).get(clickedItemIndex);
        if(productPresentationBean.getInStock().equalsIgnoreCase(ApplicationConstants.OUT_OF_STOCK)){
            EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.MOVE_ITEMS_CART, ApplicationConstants.OUT_OF_STOCK_MESSAGE);
        }
        else {
            callRemoveWishListEndpoint(productPresentationBean);
            callCartEndPoint(productPresentationBean);
        }

    }


    private void callRemoveWishListEndpoint(ProductPresentationBean productPresentationBean) {
        SharedPreferences pref = getActivity().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WishlistTransportBean.class, new AnnotatedDeserializer<WishlistTransportBean>())
                .setLenient().create();

        WishlistService wishlistService = RetrofitUtils.getRetrofit(gson).create(WishlistService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(ApplicationConstants.PRODUCT_ID, String.valueOf(productPresentationBean.getId()))
                .addFormDataPart(ApplicationConstants.CUSTOMER_ID, String.valueOf(userId))
                .build();
        Call<WishlistTransportBean> call = wishlistService.removeFromWishlist(apiToken, requestBody);

        call.enqueue(new Callback<WishlistTransportBean>() {
            @Override
            public void onResponse(Call<WishlistTransportBean> call, Response<WishlistTransportBean> response) {
                APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_REMOVED_IN_CART, productPresentationBean.getName());
                WishlistTransportBean wishlistTransportBean = response.body();
                removeWishlistFromDB(userId, productPresentationBean.getId());
            }

            @Override
            public void onFailure(Call<WishlistTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(WishlistFragment.class.getName().concat(" ").concat(throwable.getMessage()));
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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CartTransportBean.class, new AnnotatedDeserializer<CartTransportBean>())
                .setLenient().create();
        CartService cartService = RetrofitUtils.getRetrofit(gson).create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(ApplicationConstants.PRODUCT_ID, String.valueOf(productPresentationBean.getId()))
                .addFormDataPart(ApplicationConstants.CUSTOMER_ID, String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.addToCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                if (response.isSuccessful()) {
                    APIUtils.logAddToCartAnalyticsEvent(getActivity().getApplicationContext(), productPresentationBean);
                    APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_ADDED_TO_CART, productPresentationBean.getName());
                    saveCartItemsToDB(userId, productPresentationBean);
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ApplicationConstants.FAILED_TO_ADD_CART_ITEMS);
            }
        });
    }


    private void saveCartItemsToDB(int userId, ProductPresentationBean productPresentationBean) {
        Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, productPresentationBean.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productPresentationBean.getId());
            cart.setQuantity(cart.getQuantity() + 1);
            cartViewModel.insertCartItems(cart);
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cartViewModel.updateCartItems(cart);
        }
        //  EssentialsUtils.showMessage(coordinatorLayout,ApplicationConstants.CART_SUCCESS_MESSAGE);
        showSuccessSnackBar(productPresentationBean);
    }

    private void showSuccessSnackBar(ProductPresentationBean productPresentationBean) {
        Snackbar snackbar = Snackbar
                .make(view, ApplicationConstants.CART_SUCCESS_MOVE_MESSAGE, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(WishlistFragmentDirections.actionNavBottomWishlistToNavBottomCart());
                        APIUtils.logViewCartAnalyticsEvent(getActivity().getApplicationContext(), productPresentationBean);
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

    @Override
    public void onRefresh() {

        getWishlistProductsForCustomer();

    }
}

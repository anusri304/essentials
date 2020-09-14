package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essentials.R;
import com.example.essentials.activity.DeliveryAddressActivity;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.CartRecyclerViewAdapter;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.CartService;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
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

public class CartFragment extends Fragment implements CartRecyclerViewAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    CartViewModel cartViewModel;
    ProductViewModel productViewModel;
    List<Product> products = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    private static Retrofit retrofit = null;
    WishlistViewModel wishlistViewModel;
    CartRecyclerViewAdapter cartRecyclerViewAdapter;

    private SwipeRefreshLayout swipeContainer;
    List<Wishlist> wishLists = new ArrayList<>();
    View view;
    TextView totalTxtView;
    com.google.android.material.button.MaterialButton checkoutButton;
    List<CartPresentationBean> cartPresentationBeans;
    List<ProductPresentationBean> filteredProductPresentationBeans;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        view = getActivity().findViewById(android.R.id.content);
        totalTxtView = (TextView) rootView.findViewById(R.id.total_value_text_view);
        checkoutButton = (MaterialButton) rootView.findViewById(R.id.checkout_button);

        //tODO: Remove the below code if not used
        //TODO remove log.d and toast
        //TODO logo and color


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        //cartViewModel.getQuantity().observe(this,nameObserver);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

        TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
        titleView.setText(getResources().getString(R.string.your_cart_items));

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(this);



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
        getAllProducts();
        initCheckoutButton();
        observeWishlistChanges();

//        swipeContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeContainer.setRefreshing(true);
//            }
//        });
        return rootView;
    }

    private void getProductsForCustomer() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CustomerCartTransportBean.class, new AnnotatedDeserializer<CustomerCartTransportBean>())
                .setLenient().create();
        ProductService productService = RetrofitUtils.getRetrofit(gson).create(ProductService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerCartListTransportBean> call = productService.getProductsForCustomer(String.valueOf(userId), apiToken);
        //Save Products to cart
        call.enqueue(new Callback<CustomerCartListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerCartListTransportBean> call, Response<CustomerCartListTransportBean> response) {
                if (response.isSuccessful()) {
                    CustomerCartListTransportBean customerCartListTransportBeans = response.body();
                    if (customerCartListTransportBeans != null && customerCartListTransportBeans.getProducts() != null && customerCartListTransportBeans.getProducts().size() > 0) {
                        for (CustomerCartTransportBean customercartTransportBean : customerCartListTransportBeans.getProducts()) {
                            Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, Integer.parseInt(customercartTransportBean.getProductId()));
                            if (cart == null) {
                                cart = new Cart();
                                if(customercartTransportBean.getProductId()!=null && !customercartTransportBean.getProductId().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                    cart.setProductId(Integer.valueOf(customercartTransportBean.getProductId()));
                                }
                                if(customercartTransportBean.getQuantity()!=null && !customercartTransportBean.getQuantity().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                    cart.setQuantity(Integer.valueOf(customercartTransportBean.getQuantity()));
                                }
                                cart.setUserId(userId);
                                cartViewModel.insertCartItems(cart);
                            } else {
                                if(customercartTransportBean.getProductId()!=null && !customercartTransportBean.getProductId().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                    cart.setProductId(Integer.valueOf(customercartTransportBean.getProductId()));
                                }
                                if(customercartTransportBean.getQuantity()!=null && !customercartTransportBean.getQuantity().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                    cart.setQuantity(Integer.valueOf(customercartTransportBean.getQuantity()));
                                }
                                cart.setUserId(userId);
                                cartViewModel.updateCartItems(cart);
                            }
                        }

                    }
                    else {
                        APIUtils.getFirebaseCrashlytics().log(CartFragment.class.getName().concat( " ").concat(new Gson().toJson(response)));
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerCartListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(CartFragment.class.getName().concat( " ").concat(throwable.getMessage()));
            }
        });


    }

    private void initCheckoutButton() {
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DeliveryAddressActivity.class);
                startActivity(intent);
                String checkoutItems="";
                for(CartPresentationBean cartPresentationBean:EssentialsUtils.getCartPresentationBeans(cartViewModel.getAllCartItems().getValue(),filteredProductPresentationBeans)) {
                    checkoutItems= TextUtils.concat(",",String.valueOf(cartPresentationBean.getName())).toString();
                    APIUtils.logCheckoutAnalyticsEvent(getActivity().getApplicationContext(), cartPresentationBean);
                }
                APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.CHECKOUT, checkoutItems);

            }
        });
    }


    private void getAllProducts() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;
            getCartItems();

        });
    }

    private void observeWishlistChanges() {
        wishlistViewModel.getAllWishlist().observe(this, objWishlist -> {
            wishLists = objWishlist;

            drawBadgeForWishlist(wishLists.size());
        });
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

    private void getCartItems() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            if (!cartItems.isEmpty()) {
                setData(cartItems);
                int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
                drawBadge(totalQuantity);
            } else {
                TextView totalTextView = rootView.findViewById(R.id.total_title_text_view);
                totalTextView.setVisibility(View.INVISIBLE);
                TextView totalValueTextView = rootView.findViewById(R.id.total_value_text_view);
                totalValueTextView.setVisibility(View.INVISIBLE);

                MaterialButton checkoutButton = rootView.findViewById(R.id.checkout_button);
                checkoutButton.setVisibility(View.INVISIBLE);
                EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_ITEMS, ApplicationConstants.NO_ITEMS_CART);
                setData(cartItems);
                drawBadge(0);
            }

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

    private void setData(List<Cart> cartItems) {
        filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                cartItems.stream().map(cart -> cart.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        if (filteredProductPresentationBeans != null) {

            setProductData(EssentialsUtils.getCartPresentationBeans(cartItems, filteredProductPresentationBeans));
        }
        if(swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }

    }

    private void logAnalyticsEvent(List<CartPresentationBean> cartPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (CartPresentationBean cartPresentationBean : cartPresentationBeans) {
            sb.append(cartPresentationBean.getProductId());
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.CART_PRESENTATION_BEAN);
        bundle.putString(ApplicationConstants.PRODUCT_ID_LIST, sb.substring(0, sb.lastIndexOf(",")));
        APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    private void setProductData(List<CartPresentationBean> cartPresentationBeans) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        cartRecyclerViewAdapter = new CartRecyclerViewAdapter(getActivity(), cartPresentationBeans, cartViewModel, this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_cart);
        recyclerView.setAdapter(cartRecyclerViewAdapter);
        //   double totalPrice = cartPresentationBeans.stream().mapToDouble(cartPresentationBean -> Double.parseDouble(cartPresentationBean.getPrice().substring(1)) * cartPresentationBean.getQuantity()).sum();

        totalTxtView.setText(EssentialsUtils.formatTotal(EssentialsUtils.getTotal(cartPresentationBeans)));

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);

        if(cartPresentationBeans!=null && cartPresentationBeans.size()>0) {
            logAnalyticsEvent(cartPresentationBeans);
        }
    }


    @Override
    public void onListItemClick(CartPresentationBean cartPresentationBean) {
        //Remove the product from cart and add to wishlist
        callCartEndPoint(cartPresentationBean);

    }

    private void callCartEndPoint(CartPresentationBean cartPresentationBean) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CartTransportBean.class, new AnnotatedDeserializer<CartTransportBean>())
                .setLenient().create();
        CartService cartService = RetrofitUtils.getRetrofit(gson).create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(cartPresentationBean.getProductId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.removeFromCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                if (response.isSuccessful()) {
                    deleteCartItemsFromDB(userId, cartPresentationBean.getProductId());
                    Product product = productViewModel.getProduct(cartPresentationBean.getProductId());
                    APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_REMOVED_FROM_WISHLIST, product.getName());
                    APIUtils.logRemoveFromCartAnalyticsEvent(getActivity().getApplicationContext(),cartPresentationBean);
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(CartFragment.class.getName().concat( " ").concat(throwable.getMessage()));
            }
        });
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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WishlistTransportBean.class, new AnnotatedDeserializer<WishlistTransportBean>())
                .setLenient().create();
        WishlistService wishlistService = RetrofitUtils.getRetrofit(gson).create(WishlistService.class);
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
                if (response.isSuccessful()) {
                    saveWishListToDB(userId, productId);
                    Product product = productViewModel.getProduct(productId);
                    APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_ADDED_TO_WISHLIST, product.getName());

                    List<Product> analyticsList = new ArrayList<Product>();
                    analyticsList.add(product);
                    APIUtils.logAddToWishlistAnalyticsEvent(getActivity().getApplicationContext(),EssentialsUtils.getProductPresentationBeans(new ArrayList<>(analyticsList)).get(0));
                }

            }

            @Override
            public void onFailure(Call<WishlistTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(CartFragment.class.getName().concat( " ").concat(throwable.getMessage()));
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
                .make(view, ApplicationConstants.WISHLIST_SUCCESS_MOVE_MESSAGE, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(CartFragmentDirections.actionNavBottomCartToNavBottomWishlist());
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


    @Override
    public void onRefresh() {
        getProductsForCustomer();
    }
}

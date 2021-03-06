package com.example.essentials.fragment;

import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essentials.R;
import com.example.essentials.activity.ProductDetailActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Category;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.AddressService;
import com.example.essentials.service.CategoryService;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.AddressListTransportBean;
import com.example.essentials.transport.AddressTransportBean;
import com.example.essentials.transport.CategoryListTransportBean;
import com.example.essentials.transport.CategoryTransportBean;
import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.CategoryViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.essentials.utils.RetrofitUtils.getRetrofit;

public class ProductFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static Retrofit retrofit = null;
    List<ProductPresentationBean> productPresentationBeans;
    View rootView;
    ProductRecyclerViewAdapter adapter;
    ProductViewModel productViewModel;
    CartViewModel cartViewModel;
    WishlistViewModel wishlistViewModel;
    AddressViewModel addressViewModel;
    CategoryViewModel categoryViewModel;
    List<Product> products = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    List<Wishlist> wishListItems = new ArrayList<>();
    int categoryId;
    MaterialCheckBox materialCheckBox;
    private SwipeRefreshLayout swipeContainer;
    boolean showAlertDialog = true;
    boolean showSpecialAlertDialog = true;
    RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);

        } else {
            getAllCategories();
            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
            cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
            wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
            categoryViewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);
            addressViewModel = new ViewModelProvider(this, factory).get(AddressViewModel.class);
            if (getArguments() != null) {
                categoryId = ProductFragmentArgs.fromBundle(getArguments()).getCategoryId();
            }

            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

            TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
            titleView.setText(getResources().getString(R.string.app_name));

            materialCheckBox = rootView.findViewById(R.id.checkbox);

            initCheckBox();

            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            observeChanges();
            observeCartChanges();
            observeWishlistChanges();
            if (APIUtils.isUserLogged(getActivity().getApplicationContext())) {
                getDeliveryAddress();
            }

            swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

            swipeContainer.setOnRefreshListener(this);
        }
        return rootView;
    }

    private void getDeliveryAddress() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AddressTransportBean.class, new AnnotatedDeserializer<AddressTransportBean>())
                .setLenient().create();
        AddressService addressService = getRetrofit(gson).create(AddressService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");// 0 - for private mode
        String userId = String.valueOf(pref.getInt(ApplicationConstants.USER_ID, 0));
        Call<AddressListTransportBean> call = addressService.getAddressForCustomer(userId, apiToken);

        call.enqueue(new Callback<AddressListTransportBean>() {
            @Override
            public void onResponse(Call<AddressListTransportBean> call, Response<AddressListTransportBean> response) {
                if (response.isSuccessful()) {
                    AddressListTransportBean addressListTransportBean = response.body();
                    if (addressListTransportBean != null && addressListTransportBean.getAddress() != null) {
                        for (AddressTransportBean addressTransportBean : addressListTransportBean.getAddress()) {
                            Address address = addressViewModel.getAddressForId(Integer.valueOf(addressTransportBean.getAddressId()));
                            if (address == null) {
                                address = new Address();
                                address.setUserId(APIUtils.getLoggedInUserId(getActivity().getApplicationContext()));
                                address.setId(Integer.parseInt(addressTransportBean.getAddressId()));
                                address.setFirstName(addressTransportBean.getFirstname());
                                address.setLastName(addressTransportBean.getLastname());
                                address.setAddressLine1(addressTransportBean.getAddress1());
                                address.setAddressLine2(addressTransportBean.getAddress2());
                                address.setPostalCode(addressTransportBean.getPostcode());
                                address.setCity(addressTransportBean.getCity());
                                address.setCountry(addressTransportBean.getCountry());
                                addressViewModel.insertAddress(address);
                            } else {
                                address.setFirstName(addressTransportBean.getFirstname());
                                address.setLastName(addressTransportBean.getLastname());
                                address.setAddressLine1(addressTransportBean.getAddress1());
                                address.setAddressLine2(addressTransportBean.getAddress2());
                                address.setPostalCode(addressTransportBean.getPostcode());
                                address.setCity(addressTransportBean.getCity());
                                address.setCountry(addressTransportBean.getCountry());
                                addressViewModel.updateAddress(address);
                            }
                        }
                    } else {
                        APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }

                }

            }

            @Override
            public void onFailure(Call<AddressListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(throwable.getMessage()));

            }
        });
    }

    private void initCheckBox() {
        materialCheckBox.setOnCheckedChangeListener(new MaterialCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                showAlertDialog = true;
                showSpecialAlertDialog = true;
                setDataForCheckChange(checked);
            }


        });

    }

    private void setDataForCheckChange(boolean checked) {
        if (productPresentationBeans != null) {
            if (categoryId != 0) {
                productPresentationBeans = productPresentationBeans.stream().filter(productPresentationBean ->
                        String.valueOf(productPresentationBean.getCategoryId()).equals(String.valueOf(categoryId))).collect(Collectors.toList());
            }
            if (checked) {
                List<ProductPresentationBean> onSpecialProductPresentationBeans = productPresentationBeans.stream().filter(productPresentationBean -> !productPresentationBean.getSpecial().equalsIgnoreCase("")).collect(Collectors.toList());
                if (onSpecialProductPresentationBeans.size() == 0 && showSpecialAlertDialog) {
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_ON_SPECIAL_FOR_CATEGORY);
                    showSpecialAlertDialog = false;
                    setData(onSpecialProductPresentationBeans);
                } else {
                    setData(onSpecialProductPresentationBeans);
                }
            } else {
                if (productPresentationBeans.size() == 0 && showAlertDialog) {
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_FOR_CATEGORY);
                    showAlertDialog = false;
                    setData(productPresentationBeans);
                } else {
                    if (productPresentationBeans != null && productPresentationBeans.size() > 0) {
                        APIUtils.logViewItemsAnalyticsEvent(getActivity().getApplicationContext(), productPresentationBeans);
                    }
                    setData(productPresentationBeans);
                }
            }
        }
    }

    private void getAllCategories() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CategoryTransportBean.class, new AnnotatedDeserializer<CategoryTransportBean>())
                .setLenient().create();
        CategoryService categoryService = RetrofitUtils.getRetrofit(gson).create(CategoryService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0);
        Call<CategoryListTransportBean> call = categoryService.getAllCategories();

        call.enqueue(new Callback<CategoryListTransportBean>() {
            @Override
            public void onResponse(Call<CategoryListTransportBean> call, Response<CategoryListTransportBean> response) {
                if (response.isSuccessful()) {
                    CategoryListTransportBean categoryListTransportBean = response.body();
                    if (categoryListTransportBean != null && categoryListTransportBean.getCategories() != null) {
                        for (CategoryTransportBean categoryTransportBean : categoryListTransportBean.getCategories()) {
                            Category category = categoryViewModel.getCategory(Integer.valueOf(categoryTransportBean.getCategoryId()));
                            if (category == null) {
                                category = new Category();
                                category.setId(Integer.parseInt(categoryTransportBean.getCategoryId()));
                                category.setName(categoryTransportBean.getName());
                                categoryViewModel.insertCategory(category);
                            } else {
                                category.setName(categoryTransportBean.getName());
                                categoryViewModel.updateCategory(category);
                            }
                        }
                    } else {
                        APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }
                    getAllProducts();

                }

            }

            @Override
            public void onFailure(Call<CategoryListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(throwable.getMessage()));
            }
        });
    }

    public void drawBadge(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }

    private void observeCartChanges() {
        cartViewModel.getAllCartItems().observe(getViewLifecycleOwner(), objCart -> {
            cartItems = objCart;
            int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
            if (APIUtils.isUserLogged(getActivity())) {
                drawBadge(totalQuantity);
            }

        });
    }

    private void observeWishlistChanges() {
        wishlistViewModel.getAllWishlist().observe(getViewLifecycleOwner(), objWishlist -> {
            wishListItems = objWishlist;
            drawBadgeForWishlist(wishListItems.size());
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

    private void observeChanges() {
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), objProducts -> {
            products = objProducts;

            if (!products.isEmpty()) {
                List<ProductPresentationBean> onSpecialProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean -> !productPresentationBean.getSpecial().equalsIgnoreCase("")).collect(Collectors.toList());
                if (materialCheckBox.isChecked()) {
                    if (onSpecialProductPresentationBeans.size() == 0 && showSpecialAlertDialog) {
                        EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_ON_SPECIAL_FOR_CATEGORY);
                        setData(onSpecialProductPresentationBeans);
                        showSpecialAlertDialog = false;
                    } else {
                        setData(onSpecialProductPresentationBeans);
                    }
                } else {
                    List<ProductPresentationBean> productPresentationBeans = EssentialsUtils.getProductPresentationBeans(products);
                    if (productPresentationBeans.size() == 0 && showAlertDialog) {
                        EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_FOR_CATEGORY);
                        setData(productPresentationBeans);
                        showAlertDialog = false;
                    } else {
                        setData(productPresentationBeans);
                    }
                }

            }
        });
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
                                cart.setProductId(Integer.valueOf(customercartTransportBean.getProductId()));
                                cart.setQuantity(Integer.valueOf(customercartTransportBean.getQuantity()));
                                cart.setUserId(userId);
                                cartViewModel.insertCartItems(cart);
                            } else {
                                cart.setProductId(Integer.valueOf(customercartTransportBean.getProductId()));
                                cart.setQuantity(Integer.valueOf(customercartTransportBean.getQuantity()));
                                cart.setUserId(userId);
                                cartViewModel.updateCartItems(cart);
                            }
                        }

                    } else {
                        APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }
                    getWishlistProductsForCustomer();
                }
            }

            @Override
            public void onFailure(Call<CustomerCartListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(throwable.getMessage()));
            }
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
                        }
                    } else {
                        APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerWishListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(throwable.getMessage()));
            }
        });
    }


    private void getAllProducts() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ProductTransportBean.class, new AnnotatedDeserializer<ProductTransportBean>())
                .setLenient().create();
        ProductService productService = getRetrofit(gson).create(ProductService.class);
        Call<ProductListTransportBean> call = productService.getAllProducts();

        call.enqueue(new Callback<ProductListTransportBean>() {
            @Override
            public void onResponse(Call<ProductListTransportBean> call, Response<ProductListTransportBean> response) {
                if (response.isSuccessful()) {
                    ProductListTransportBean productTransportBeans = response.body();
                    productPresentationBeans = new ArrayList<ProductPresentationBean>();
                    if (productTransportBeans != null && productTransportBeans.getProducts().size() > 0) {
                        for (ProductTransportBean productTransportBean : productTransportBeans.getProducts()) {
                            ProductPresentationBean productPresentationBean = new ProductPresentationBean();
                            if (productTransportBean.getProductId() != null && !productTransportBean.getProductId().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                productPresentationBean.setId(Integer.valueOf(productTransportBean.getProductId()));
                            } else {
                                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.NULL_PRODUCT_ID));
                            }
                            productPresentationBean.setImage(productTransportBean.getImage().replace("http://localhost/OpenCart/", ApplicationConstants.BASE_URL));
                           // productPresentationBean.setImage(productTransportBean.getImage());
                            // productPresentationBean.setImage("http://10.0.75.1/Opencart/image/cache/catalog/demo/canon_eos_5d_1-228x228.jpg");
                            productPresentationBean.setName(productTransportBean.getName());
                            productPresentationBean.setPrice(productTransportBean.getPrice());
                            if (productTransportBean.getCategoryId() != null && !productTransportBean.getCategoryId().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                productPresentationBean.setCategoryId(Integer.valueOf(productTransportBean.getCategoryId()));
                            } else {
                                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.NULL_CATEGORY_ID));
                            }
                            productPresentationBean.setDescription(productTransportBean.getDescription());
                            if (productTransportBean.getSpecial() != null && !productTransportBean.getSpecial().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
                                productPresentationBean.setSpecial(productTransportBean.getSpecial().equalsIgnoreCase(ApplicationConstants.FALSE) ? "" : productTransportBean.getSpecial());
                            } else {
                                APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.NULL_SPECIAL));
                            }
                            productPresentationBean.setDiscPerc(productTransportBean.getDiscPerc());
                            productPresentationBean.setInStock(productTransportBean.getInStock());
                            productPresentationBeans.add(productPresentationBean);
                        }
                        saveorUpdateProduct(productPresentationBeans);
                    } else {
                        APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(ApplicationConstants.ERROR_RETRIEVE_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                // No data is retrieved. Check if there is no internet
                if (!NetworkUtils.isNetworkConnected(getActivity())) {
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
                } else { // If there is internet then there is an error retrieving data. display error retrieve message
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.DATA_ERROR, ApplicationConstants.ERROR_RETRIEVE_MESSAGE);
                    APIUtils.getFirebaseCrashlytics().log(ProductFragment.class.getName().concat(" ").concat(throwable.getMessage()));
                }


            }
        });

    }

    private void saveorUpdateProduct(List<ProductPresentationBean> productPresentationBeans) {

        // Initially log for special items as they are shown.
        APIUtils.logViewItemsAnalyticsEvent(getActivity().getApplicationContext(), productPresentationBeans.stream().filter(productPresentationBean -> !productPresentationBean.getSpecial().equalsIgnoreCase("")).collect(Collectors.toList()));
        for (ProductPresentationBean productPresentationBean : productPresentationBeans) {
            Product product = productViewModel.getProduct(productPresentationBean.getId());
            if (product != null) {
                product.setId(Integer.valueOf(productPresentationBean.getId()));
                product.setName(EssentialsUtils.replaceSpecialCharacter(productPresentationBean.getName()));
                product.setPrice(productPresentationBean.getPrice());
                product.setCategoryId(Integer.valueOf(productPresentationBean.getCategoryId()));
                product.setDescription(EssentialsUtils.replaceSpecialCharacter(productPresentationBean.getDescription()));
                product.setSpecial(productPresentationBean.getSpecial());
                product.setDiscPerc(productPresentationBean.getDiscPerc());
                product.setInStock(productPresentationBean.getInStock());
                productViewModel.updateProduct(product, getActivity().getApplicationContext(), productPresentationBean.getImage());
            } else {
                product = new Product();
                product.setId(Integer.valueOf(productPresentationBean.getId()));
                product.setName(EssentialsUtils.replaceSpecialCharacter(productPresentationBean.getName()));
                product.setPrice(productPresentationBean.getPrice());
                product.setDescription(EssentialsUtils.replaceSpecialCharacter(productPresentationBean.getDescription()));
                product.setSpecial(productPresentationBean.getSpecial());
                product.setCategoryId(Integer.valueOf(productPresentationBean.getCategoryId()));
                product.setDiscPerc(productPresentationBean.getDiscPerc());
                product.setInStock(productPresentationBean.getInStock());
                productViewModel.insertProduct(product, getActivity().getApplicationContext(), productPresentationBean.getImage());
            }
        }
        if (APIUtils.isUserLogged(getActivity().getApplicationContext())) {
            getProductsForCustomer();
        }
    }

    private void logAnalyticsEvent(List<ProductPresentationBean> productPresentationBeans, ProductPresentationBean selectedProductPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, selectedProductPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(selectedProductPresentationBean.getId()));
        APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);
    }

    private void setData(List<ProductPresentationBean> productPresentationBeans) {
        //   this.productPresentationBeans = productPresentationBeans;
        if (categoryId != 0) {
            productPresentationBeans = productPresentationBeans.stream().filter(productPresentationBean ->
                    String.valueOf(productPresentationBean.getCategoryId()).equals(String.valueOf(categoryId))).collect(Collectors.toList());
        }
        if (!materialCheckBox.isChecked()) {
            if (productPresentationBeans.size() == 0 && showAlertDialog == true) {
                EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_FOR_CATEGORY);
                showAlertDialog = false;
                return;
            }
        } else {
            if (productPresentationBeans.size() == 0 && showSpecialAlertDialog == true) {
                EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_PRODUCTS, ApplicationConstants.NO_PRODUCT_ON_SPECIAL_FOR_CATEGORY);
                showSpecialAlertDialog = false;
                return;
            }
        }

        adapter = new ProductRecyclerViewAdapter(getActivity(), productPresentationBeans);
        recyclerView = rootView.findViewById(R.id.rv_products);
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);

        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        if (recyclerView != null && productPresentationBeans != null) {
            Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            // putting recyclerview position
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
            // putting recyclerview items
            savedInstanceState.putParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(productPresentationBeans));
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID);
        // getting recyclerview items
        productPresentationBeans = savedInstanceState.getParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID);
        // Restoring adapter items
        if (recyclerView != null && productPresentationBeans != null) {
            setData(productPresentationBeans);
            // Restoring recycler view position
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }

    public void filter(String query) {
        if (adapter != null) {
            adapter.performFilter(query);
            logAnalyticsEvent(query);
        }

    }

    @Override
    public void onRefresh() {
        setDataForCheckChange(materialCheckBox.isChecked());
    }


    private void logAnalyticsEvent(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }
}

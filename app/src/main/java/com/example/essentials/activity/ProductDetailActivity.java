package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Category;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.CartService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.WishlistTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.CategoryViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductDetailActivity extends AppCompatActivity {
    ProductPresentationBean productPresentationBean;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout coordinatorLayout;
    View mUpButton;
    private static Retrofit retrofit = null;
    WishlistViewModel wishlistViewModel;
    CartViewModel cartViewModel;
    CategoryViewModel categoryViewModel;
    MaterialButton addToCartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getIntent() != null) {
            productPresentationBean = getIntent().getParcelableExtra(ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
            collapsingToolbarLayout = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));
            collapsingToolbarLayout.setTitle(productPresentationBean.getName());

            collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

            coordinatorLayout = findViewById(R.id.coordinatorLayout);

            mUpButton = findViewById(R.id.app_bar);

            initLayout();

            ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
            wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
            cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
            categoryViewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);
            initButton();
        } else {
            closeOnError();
        }

        logViewItemAnalyticsEvent();

    }

    private void initButton() {
        if(productPresentationBean.getInStock().equalsIgnoreCase(ApplicationConstants.OUT_OF_STOCK)){
            addToCartButton.setEnabled(false);
        }
    }

    private void logViewItemAnalyticsEvent() {
        Category category = categoryViewModel.getCategory(Integer.valueOf(productPresentationBean.getCategoryId()));
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(ProductDetailActivity.this).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, ApplicationConstants.PRODUCT_DETAILS_NOT_AVAILABLE, Toast.LENGTH_SHORT).show();
    }

    private void initLayout() {
        TextView txtViewTitle = findViewById(R.id.product_title_txtview);
        txtViewTitle.setText(productPresentationBean.getName());
        addToCartButton = (MaterialButton) findViewById(R.id.add_to_cart_button);
        setProductImageView();
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setProductView();
        setProductDescImageView();
        setWishListButton();
        setCartButton();
    }

    private void setCartButton() {
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (APIUtils.isUserLogged(ProductDetailActivity.this)) {
                    callCartEndPoint();
                } else {
                    EssentialsUtils.showMessageAlertDialog(ProductDetailActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_CART);
                }
            }
        });
    }

    private void callCartEndPoint() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CartTransportBean.class, new AnnotatedDeserializer<CartTransportBean>())
                .setLenient().create();
        CartService cartService = RetrofitUtils.getRetrofit(gson).create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productPresentationBean.getId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.addToCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                if (response.isSuccessful()) {
                    CartTransportBean cartTransportBean = response.body();
                    saveCartItemsToDB(userId, productPresentationBean.getId());
                    APIUtils.logAddToCartAnalyticsEvent(ProductDetailActivity.this, productPresentationBean);
                    APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_ADDED_TO_CART, productPresentationBean.getName());
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductDetailActivity.class.getName().concat( " ").concat(throwable.getMessage()));
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
        showCartSnackBar();
    }

    private void setWishListButton() {
        MaterialButton addToWishList = (MaterialButton) findViewById(R.id.add_wish_list_button);
        addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (APIUtils.isUserLogged(ProductDetailActivity.this)) {
                    callWishListEndpoint();
                } else {
                    EssentialsUtils.showMessageAlertDialog(ProductDetailActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_WISHLIST);
                }

            }
        });
    }

    private void callWishListEndpoint() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WishlistTransportBean.class, new AnnotatedDeserializer<WishlistTransportBean>())
                .setLenient().create();
        WishlistService wishlistService = RetrofitUtils.getRetrofit(gson).create(WishlistService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productPresentationBean.getId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<WishlistTransportBean> call = wishlistService.addToWishlist(apiToken, requestBody);

        call.enqueue(new Callback<WishlistTransportBean>() {
            @Override
            public void onResponse(Call<WishlistTransportBean> call, Response<WishlistTransportBean> response) {
                WishlistTransportBean wishlistTransportBean = response.body();
                saveWishListToDB(userId, productPresentationBean.getId());

                APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PRODUCT_NAME_ADDED_TO_WISHLIST, productPresentationBean.getName());
                APIUtils.logAddToWishlistAnalyticsEvent(ProductDetailActivity.this,productPresentationBean);
            }

            @Override
            public void onFailure(Call<WishlistTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ProductDetailActivity.class.getName().concat( " ").concat(throwable.getMessage()));
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
        showWishlistSnackBar();
    }

    private void showWishlistSnackBar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, ApplicationConstants.WISHLIST_SUCCESS_MESSAGE, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                        intent.putExtra(ApplicationConstants.LAUNCH_WISH_LIST, true);
                        startActivity(intent);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


    private void showCartSnackBar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, ApplicationConstants.CART_SUCCESS_MESSAGE, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.view), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                        intent.putExtra(ApplicationConstants.LAUNCH_CART, true);
                        startActivity(intent);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void setProductDescImageView() {
        ImageView forwardButton = (ImageView) findViewById(R.id.arrowForward);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductDescriptionActivity.class);
                intent.putExtra(ApplicationConstants.PRODUCT_DESC, productPresentationBean.getDescription());
                intent.putExtra(ApplicationConstants.PRODUCT_NAME, productPresentationBean.getName());
                startActivity(intent);
            }
        });
    }

    private void setProductView() {
        TextView txtViewSpecialPrice = findViewById(R.id.product_special_price_txtView);
        TextView txtViewDiscPerc = findViewById(R.id.product_disc_perc_txtView);
        TextView txtViewPrice = findViewById(R.id.product_price_txtView);
        TextView productStockTxtView = (TextView) findViewById(R.id.product_stock_txtview);
        if (productPresentationBean.getSpecial().equalsIgnoreCase("")) {
            txtViewSpecialPrice.setVisibility(View.GONE);
            txtViewDiscPerc.setVisibility(View.GONE);
            txtViewPrice.setPadding(25, 20, 0, 0);
            txtViewPrice.setText(productPresentationBean.getPrice());
        } else {
            txtViewPrice.setPaintFlags(txtViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtViewPrice.setText(productPresentationBean.getPrice());
            txtViewSpecialPrice.setText(productPresentationBean.getSpecial());
            txtViewPrice.setTextColor(getResources().getColor(R.color.red));
            txtViewDiscPerc.setText(TextUtils.concat(productPresentationBean.getDiscPerc(), " ", ApplicationConstants.OFF));
        }

        productStockTxtView.setText(productPresentationBean.getInStock().equalsIgnoreCase("Yes") ? ApplicationConstants.IN_STOCK : ApplicationConstants.OUT_OF_STOCK);
    }

    private void setProductImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.image_product_detail);
        Glide.with(this)
                .load(productPresentationBean.getImage())
                //  .load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }
}

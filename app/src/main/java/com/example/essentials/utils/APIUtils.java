package com.example.essentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIUtils {
    private static Retrofit retrofit = null;

    private static FirebaseAnalytics firebaseAnalytics =null;
    private static FirebaseCrashlytics firebaseCrashlytics =null;

    public static Retrofit getRetrofit() {
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

    public  static List<ProductPresentationBean>  getAllProducts() {
        ProductService productService = getRetrofit().create(ProductService.class);
        Call<ProductListTransportBean> call = productService.getAllProducts();
        List<ProductPresentationBean> productPresentationBeans = new ArrayList<ProductPresentationBean>();
        call.enqueue(new Callback<ProductListTransportBean>() {
            @Override
            public void onResponse(Call<ProductListTransportBean> call, Response<ProductListTransportBean> response) {
                ProductListTransportBean productTransportBeans = response.body();
                if (productTransportBeans != null) {
                    for (ProductTransportBean productTransportBean : productTransportBeans.getProducts()) {
                        ProductPresentationBean productPresentationBean = new ProductPresentationBean();
                        productPresentationBean.setId(Integer.valueOf(productTransportBean.getProductId()));
                        //TODO: Replace the path in Opencart
                        productPresentationBean.setImage(productTransportBean.getImage().replace("http://localhost/OpenCart/", ApplicationConstants.BASE_URL));
                        // productPresentationBean.setImage("http://10.0.75.1/Opencart/image/cache/catalog/demo/canon_eos_5d_1-228x228.jpg");
                        productPresentationBean.setName(productTransportBean.getName());
                        productPresentationBean.setPrice(productTransportBean.getPrice());
                        //TODO: Get full description
                        productPresentationBean.setDescription(productTransportBean.getDescription());
                        //TODO: Only get special products
                        productPresentationBean.setSpecial(productTransportBean.getSpecial().equals(ApplicationConstants.FALSE) ? "" : productTransportBean.getSpecial());
                        //TODO: get disc perc
                        productPresentationBean.setDiscPerc(productTransportBean.getDiscPerc());
                        //TODO: get inStock

                        //TODO: REMOVE hardcoded tag in all activity.
                        //TODO check for hardcoded strings
                        //Todo remove log.d and toast and echo in open cart
                        productPresentationBean.setInStock(productTransportBean.getInStock());
                        productPresentationBeans.add(productPresentationBean);
                    }

                }
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ApplicationConstants.FAILED_TO_GET_ALL_PRODUCTS);
            }
        });
        return productPresentationBeans;
    }

    public static List<CustomerCartTransportBean> getProductsForCustomer(Context context) {
        ProductService productService = getRetrofit().create(ProductService.class);
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerCartListTransportBean> call = productService.getProductsForCustomer(String.valueOf(userId), apiToken);
        List<CustomerCartTransportBean> customerCartTransportBeanList = new ArrayList<CustomerCartTransportBean>();
        //Save Products to cart
        call.enqueue(new Callback<CustomerCartListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerCartListTransportBean> call, Response<CustomerCartListTransportBean> response) {
                CustomerCartListTransportBean customerCartListTransportBeans = response.body();
                if(customerCartListTransportBeans !=null) {
                    customerCartTransportBeanList.addAll(customerCartListTransportBeans.getProducts());
                }
            }

            @Override
            public void onFailure(Call<CustomerCartListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ApplicationConstants.FAILED_TO_GET_PRODUCTS_FOR_CUSTOMER);
            }
        });
        return customerCartTransportBeanList;
    }

    public static List<CustomerWishTransportBean> getWishlistProductsForCustomer(Context context) {
        WishlistService wishlistService = getRetrofit().create(WishlistService.class);
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerWishListTransportBean> call = wishlistService.getWishListProductsForCustomer(String.valueOf(userId), apiToken);

        List<CustomerWishTransportBean> customerWishListTransportBeanList = new ArrayList<CustomerWishTransportBean>();
        //Save Products to cart
        call.enqueue(new Callback<CustomerWishListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerWishListTransportBean> call, Response<CustomerWishListTransportBean> response) {
                CustomerWishListTransportBean customerWishListTransportBeans = response.body();
                if(customerWishListTransportBeans !=null){
                    customerWishListTransportBeanList.addAll(customerWishListTransportBeans.getProducts());
                }

            }

            @Override
            public void onFailure(Call<CustomerWishListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ApplicationConstants.FAILED_TO_GET_WISHLIST_PRODUCTS);
            }
        });
        return customerWishListTransportBeanList;
    }


    public static boolean isUserLogged(Context context){
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        return apiToken.equalsIgnoreCase("") ? false:true;


    }

    public static String getLoggedInUserName(Context context){
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getString(ApplicationConstants.USERNAME, "");
    }

    public static int getLoggedInUserId(Context context){
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getInt(ApplicationConstants.USER_ID, 0);
    }

    public static String getLoggedInToken(Context context){
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getString(ApplicationConstants.API_TOKEN, "");
    }


    public static void logViewItemsAnalyticsEvent(Context context, List<ProductPresentationBean> productPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (ProductPresentationBean productPresentationBean : productPresentationBeans) {
            sb.append(productPresentationBean.getId());
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
        bundle.putString(ApplicationConstants.ITEM_ID_LIST, sb.substring(0, sb.lastIndexOf(",")));
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    public static void logAddToCartAnalyticsEvent(Context context,ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public static void logCheckoutAnalyticsEvent(Context context,CartPresentationBean cartPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, cartPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public static void logRemoveFromCartAnalyticsEvent(Context context,CartPresentationBean cartPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, cartPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, bundle);
    }

    public static void logAddToWishlistAnalyticsEvent(Context context,ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
    }


    public static void logViewCartAnalyticsEvent(Context context,ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_CART, bundle);
    }




    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        if(firebaseAnalytics ==null) {
            firebaseAnalytics=  FirebaseAnalytics.getInstance(context);
        }
        return firebaseAnalytics;
    }

    public static FirebaseCrashlytics getFirebaseCrashlytics() {
        if(firebaseCrashlytics ==null) {
            firebaseCrashlytics=  FirebaseCrashlytics.getInstance();
        }
        return firebaseCrashlytics;
    }
}

package com.example.essentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
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
                        productPresentationBean.setDiscPerc(productTransportBean.getSpecial().equals(ApplicationConstants.FALSE) ? "" : productTransportBean.getDiscPerc());
                        //TODO: get inStock
                        productPresentationBean.setInStock(productTransportBean.getInStock());
                        productPresentationBeans.add(productPresentationBean);
                    }

                }
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
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
                //Anandhi add null check
                // Log.i(TAG, "onResponse: " + customerCartListTransportBeans.getProducts().size());
                if(customerCartListTransportBeans !=null) {
                    customerCartTransportBeanList.addAll(customerCartListTransportBeans.getProducts());
                }
            }

            @Override
            public void onFailure(Call<CustomerCartListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
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
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });
        return customerWishListTransportBeanList;
    }


    public static boolean isUserLogged(Context context){
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        return apiToken.equalsIgnoreCase("") ? false:true;


    }

}

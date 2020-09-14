package com.example.essentials.utils;

import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.transport.AddressTransportBean;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.transport.CategoryTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.LoginTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.transport.WishlistTransportBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    private static Retrofit retrofit = null;

    private static OkHttpClient getInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }


    public static Retrofit getRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForProduct() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ProductTransportBean.class, new AnnotatedDeserializer<ProductTransportBean>())
                .setLenient().create();
            return new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(getInterceptor())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
    }

    public static Retrofit getRetrofitForAddress() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AddressTransportBean.class, new AnnotatedDeserializer<AddressTransportBean>())
                .setLenient().create();
            return new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(getInterceptor())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
    }

    public static Retrofit getRetrofitForCart() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CartTransportBean.class, new AnnotatedDeserializer<CartTransportBean>())
                .setLenient().create();
         return new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(getInterceptor())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
    }

    public static Retrofit getRetrofitForCategory() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CategoryTransportBean.class, new AnnotatedDeserializer<CategoryTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForCustomerCart() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CustomerCartTransportBean.class, new AnnotatedDeserializer<CustomerCartTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForCustomerWish() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CustomerWishTransportBean.class, new AnnotatedDeserializer<CustomerWishTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForLogin() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LoginTransportBean.class, new AnnotatedDeserializer<LoginTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForRegister() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RegisterTransportBean.class, new AnnotatedDeserializer<RegisterTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofitForWishList() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WishlistTransportBean.class, new AnnotatedDeserializer<WishlistTransportBean>())
                .setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(ApplicationConstants.BASE_URL)
                .client(getInterceptor())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}

package com.example.essentials.utils;

import com.google.gson.Gson;

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


}

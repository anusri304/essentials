package com.example.essentials.service;

import com.example.essentials.transport.CartTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CartService {
    @POST("index.php?route=api/cart/add")
    Call<CartTransportBean> addToCart(@Query("api_token") String apiToken, @Body RequestBody cartBean);

    @POST("index.php?route=api/cart/removeFromCart")
    Call<CartTransportBean> removeFromCart(@Query("api_token") String apiToken, @Body RequestBody cartBean);
}

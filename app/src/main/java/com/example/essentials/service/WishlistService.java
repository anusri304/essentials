package com.example.essentials.service;

import com.example.essentials.transport.WishlistTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WishlistService {

    @POST("index.php?route=api/wishlist/add")
    Call<WishlistTransportBean> addToWishlist(@Query("api_token") String apiToken,@Body RequestBody wishListBean);
}

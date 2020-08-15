package com.example.essentials.service;

import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.WishlistTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WishlistService {

    @POST("index.php?route=api/wishlist/add")
    Call<WishlistTransportBean> addToWishlist(@Query("api_token") String apiToken,@Body RequestBody wishListBean);


    @POST("index.php?route=api/wishlist/remove")
    Call<WishlistTransportBean> removeFromWishlist(@Query("api_token") String apiToken,@Body RequestBody wishListBean);

    @GET("index.php?route=api/wishlist/getWishlistProductsForCustomer")
    Call<CustomerWishListTransportBean> getWishListProductsForCustomer(@Query("customerId") String customerId, @Query("api_token") String api_token);
}

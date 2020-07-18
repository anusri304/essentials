package com.example.essentials.service;

import com.example.essentials.transport.LoginTransportBean;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {

    @GET("index.php?route=api/cart/productsByCustomer")
    Call<ProductListTransportBean> getProductsForCustomer(@Query("customerId") String customerId, @Query("api_token") String api_token);

    @GET("index.php?route=api/product")
    Call<ProductListTransportBean> getAllProducts();

}

package com.example.essentials.service;

import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.ProductListTransportBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {

    @GET("index.php?route=api/cart/productsByCustomer")
    Call<CustomerCartListTransportBean> getProductsForCustomer(@Query("customerId") String customerId, @Query("api_token") String api_token);

    @GET("index.php?route=api/product")
    Call<ProductListTransportBean> getAllProducts();

}

package com.example.essentials.service;

import com.example.essentials.transport.OrderTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderService {
    @POST("index.php?route=api/order/addOrder")
    Call<OrderTransportBean> addOrder(@Query("api_token") String apiToken, @Body RequestBody orderBean);

    @POST("index.php?route=api/order/addOrderProducts")
    Call<OrderTransportBean> addOrderProducts(@Query("api_token") String apiToken, @Body RequestBody orderProductBean);

}

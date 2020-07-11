package com.example.essentials.service;

import com.example.essentials.activity.bean.LoginInputBean;
import com.example.essentials.transport.LoginTransportBean;
import com.example.essentials.transport.RegisterTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginCustomerService {

    @POST("index.php?route=api/login")
    Call<LoginTransportBean> loginCustomer(@Body RequestBody loginInputBean);
}


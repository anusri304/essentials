package com.example.essentials.service;

import com.example.essentials.transport.LoginTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginCustomerService {

    @POST("index.php?route=api/login")
    Call<LoginTransportBean> loginCustomer(@Body RequestBody loginInputBean);
}


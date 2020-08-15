package com.example.essentials.service;

import com.example.essentials.transport.RegisterTransportBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RegisterCustomerService {
    @GET("index.php?route=api/register")
    Call<RegisterTransportBean> registerCustomer(@Query("email") String email,@Query("firstname") String firstname,@Query("lastname") String lastname,@Query("telephone") String telephone,@Query("password") String password);
}

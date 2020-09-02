package com.example.essentials.service;

import com.example.essentials.transport.AddressTransportBean;
import com.example.essentials.transport.CartTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AddressService {
    @POST("index.php?route=api/address/addAddress")
    Call<AddressTransportBean> addAddress(@Query("api_token") String apiToken,@Body RequestBody addressBean);
}

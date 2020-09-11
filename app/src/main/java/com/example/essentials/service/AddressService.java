package com.example.essentials.service;

import com.example.essentials.transport.AddressListTransportBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AddressService {
    @POST("index.php?route=api/address/addAddress")
    Call<AddressListTransportBean> addAddress(@Query("api_token") String apiToken, @Body RequestBody addressBean);

    @POST("index.php?route=api/address/deleteAddress")
    Call<AddressListTransportBean> deleteAddress(@Query("api_token") String apiToken, @Body RequestBody addressBean);

    @GET("index.php?route=api/address/getAddressesForCustomer")
    Call<AddressListTransportBean> getAddressForCustomer(@Query("customerId") String customerId, @Query("api_token") String apiToken);

}

package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class CustomerWishListTransportBean {
    int id;
    @SerializedName("products")
    List<CustomerWishTransportBean> products;
}

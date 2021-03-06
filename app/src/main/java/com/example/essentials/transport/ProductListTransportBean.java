package com.example.essentials.transport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class ProductListTransportBean {
    int id;
    @SerializedName("products")
    List<ProductTransportBean> products;
}

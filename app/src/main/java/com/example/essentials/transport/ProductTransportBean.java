package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTransportBean {
    @JsonRequired
    String image;
    @JsonRequired
    String special;
    @SerializedName("product_id")
    @JsonRequired
    String productId;
    @SerializedName("category_id")
    @JsonRequired
    String categoryId;
    @JsonRequired
    String name;
    @JsonRequired
    String description;
    @JsonRequired
    String price;
    @JsonRequired
    String discPerc;
    @JsonRequired
    String inStock;
}

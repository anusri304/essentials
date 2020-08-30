package com.example.essentials.transport;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTransportBean {
    String image;
    String special;
    @SerializedName("product_id")
    String productId;
    @SerializedName("category_id")
    String categoryId;
    String name;
    String description;
    String quantity;
   String price;
   String discPerc;
   String inStock;
}

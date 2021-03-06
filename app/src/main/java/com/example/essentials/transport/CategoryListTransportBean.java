package com.example.essentials.transport;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class CategoryListTransportBean {
    int id;
    @SerializedName("categories")
    List<CategoryTransportBean> categories;
}

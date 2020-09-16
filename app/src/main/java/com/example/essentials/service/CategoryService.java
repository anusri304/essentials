package com.example.essentials.service;

import com.example.essentials.transport.CategoryListTransportBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoryService {
    @GET("index.php?route=api/category/getCategories")
    Call<CategoryListTransportBean> getAllCategories();
}

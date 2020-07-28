package com.example.essentials.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;

import java.util.List;

@Dao
public interface ProductDao {
    @Delete
    void delete(Product product);

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Query("SELECT * from Product" )
    List<Product> getAllProducts();

    @Query("SELECT * from Product where id=:productId")
    Product getProduct(int productId);
}

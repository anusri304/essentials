package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Delete
    void delete(Product product);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(Product product);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateProduct(Product product);

    @Query("SELECT * from Product" )
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * from Product where id=:productId")
    Product getProduct(int productId);

    @Query("select * FROM product where special !='' AND inStock = 'yes' LIMIT 1" )
    Product getPromotionProduct();
}

package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Cart;

import java.util.List;

@Dao
public interface CartDao {
    @Delete
    void delete(Cart cart);

    @Insert
    void insertCartItems(Cart cart);

    @Update
    void updateCart(Cart cart);

    @Query("SELECT * from Cart where userId=:userId and productId=:productId")
    Cart getCartItemsForUserAndProduct(int userId, int productId);

    @Query("SELECT * from Cart" )
    LiveData<List<Cart>> getAllCartItems();

}

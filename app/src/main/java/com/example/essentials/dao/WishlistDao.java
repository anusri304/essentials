package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Wishlist;

import java.util.List;

@Dao
public interface WishlistDao {
    @Delete
    void delete(Wishlist user);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertWishlist(Wishlist wishlist);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateWishlist(Wishlist wishlist);

    @Query("SELECT * from Wishlist where userId=:userId and productId=:productId")
    Wishlist getWishlistForUserAndProduct(int userId,int productId);

    @Query("SELECT * from Wishlist" )
    LiveData<List<Wishlist>> getAllWishlist();

}

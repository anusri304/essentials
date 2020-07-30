package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;
import com.example.essentials.domain.Wishlist;

import java.util.List;

@Dao
public interface WishlistDao {
    @Delete
    void delete(Wishlist user);

    @Insert
    void insertWishlist(Wishlist wishlist);

    @Update
    void updateWishlist(Wishlist wishlist);

    @Query("SELECT * from Wishlist where userId=:userId and productId=:productId")
    Wishlist getWishlistForUserAndProduct(int userId,int productId);

    @Query("SELECT * from Wishlist" )
    LiveData<List<Wishlist>> getAllWishlist();


//    @Query("SELECT * from Movie where id=:movieId")
//    LiveData<Movie> getMovie(int movieId);
}

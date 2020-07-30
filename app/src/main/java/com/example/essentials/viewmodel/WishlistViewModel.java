package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.Wishlist;
import com.example.essentials.repository.WishlistRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class WishlistViewModel extends AndroidViewModel {
    private WishlistRepository wishlistRepository;
    int wishlistId;
    Wishlist wishlist;
    LiveData<List<Wishlist>> wishlists;

    public WishlistViewModel(@NonNull Application application) {
        super(application);
        wishlistRepository = new WishlistRepository(application);
        wishlists = wishlistRepository.getAllWishlist();
    }

    public WishlistViewModel(@NonNull Application application, int userId, int productId) {
        super(application);
        wishlistRepository = new WishlistRepository(application);
        wishlist = wishlistRepository.getWishlistForUserAndProduct(userId,productId);
        wishlists = wishlistRepository.getAllWishlist();
    }

    public void insertWishlist(Wishlist wishlist){
        wishlistRepository.insertWishlist(wishlist);
    }

    public void updateWishlist(Wishlist wishlist){
        wishlistRepository.updateWishlist(wishlist);
    }

    public Wishlist getWishlistForUserAndProduct(int userId, int productId){
        return wishlistRepository.getWishlistForUserAndProduct(userId,productId);
    }

    public LiveData<List<Wishlist>> getAllWishlist() {
        return wishlists;
    }

}

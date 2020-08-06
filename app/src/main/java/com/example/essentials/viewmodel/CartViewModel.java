package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.repository.CartRepository;
import com.example.essentials.repository.WishlistRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    int cartId;
    Cart cart;
    LiveData<List<Cart>> cartItems;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        cartItems = cartRepository.getAllCartItems();
    }

    public CartViewModel(@NonNull Application application, int userId, int productId) {
        super(application);
        cartRepository = new CartRepository(application);
        cart = cartRepository.getCartItemsForUserAndProduct(userId,productId);
        cartItems = cartRepository.getAllCartItems();
    }

    public void insertCartItems(Cart cart){
        cartRepository.insertCartItems(cart);
    }

    public void updateCartItems(Cart cart){
        cartRepository.updateCartItems(cart);
    }

    public Cart getCartItemsForUserAndProduct(int userId, int productId){
        return cartRepository.getCartItemsForUserAndProduct(userId,productId);
    }

    public LiveData<List<Cart>> getAllCartItems() {
        return cartItems;
    }

}

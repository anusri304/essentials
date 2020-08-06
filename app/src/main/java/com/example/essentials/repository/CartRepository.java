package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.CartDao;
import com.example.essentials.dao.WishlistDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.executors.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class CartRepository {
    private CartDao cartDao;

    public CartRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        cartDao = db.cartDao();
    }
    public void insertCartItems(Cart cart) {
        AppExecutors.getInstance().diskIO().execute(() -> cartDao.insertCartItems(cart));
    }

    public void updateCartItems(Cart cart) {
        AppExecutors.getInstance().diskIO().execute(() -> cartDao.updateCart(cart));

    }

    public Cart getCartItemsForUserAndProduct(int userId,int productId) {
        Cart cart = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<Cart> future = executorService.submit(new CartRepository.MyInfoCallable(userId,productId, cartDao));
            cart = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cart;
    }

    public LiveData<List<Cart>> getAllCartItems() {
        return cartDao.getAllCartItems();
    }

    private static class MyInfoCallable implements Callable<Cart> {

        int userId;
        int productId;
        CartDao cartDao;

        public MyInfoCallable(int userId,int productId, CartDao cartDao) {
            this.userId = userId;
            this.productId = productId;
            this.cartDao = cartDao;
        }

        @Override
        public Cart call() throws Exception {
            return cartDao.getCartItemsForUserAndProduct(userId,productId);
        }
    }
}

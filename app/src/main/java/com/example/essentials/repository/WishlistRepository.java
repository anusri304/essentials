package com.example.essentials.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.ProductDao;
import com.example.essentials.dao.UserDao;
import com.example.essentials.dao.WishlistDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.executors.AppExecutors;
import com.example.essentials.utils.ApplicationConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class WishlistRepository {
    private WishlistDao wishlistDao;

    public WishlistRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        wishlistDao = db.wishlistDao();
    }
    public void insertWishlist(Wishlist wishlist) {
        AppExecutors.getInstance().diskIO().execute(() -> wishlistDao.insertWishlist(wishlist));
    }

    public void updateWishlist(Wishlist wishlist) {
        AppExecutors.getInstance().diskIO().execute(() -> wishlistDao.updateWishlist(wishlist));

    }

    public Wishlist getWishlistForUserAndProduct(int userId,int productId) {

//        AppExecutors.getInstance().diskIO().execute(() -> userDao.getUser(customerId));
//        return userDao.getUser(customerId);
        Wishlist wishlist = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<Wishlist> future = executorService.submit(new WishlistRepository.MyInfoCallable(userId,productId, wishlistDao));
            wishlist = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wishlist;
    }

    public LiveData<List<Wishlist>> getAllWishlist() {
        return wishlistDao.getAllWishlist();
    }

    private static class MyInfoCallable implements Callable<Wishlist> {

        int userId;
        int productId;
        WishlistDao wishlistDao;

        public MyInfoCallable(int userId,int productId, WishlistDao wishlistDao) {
            this.userId = userId;
            this.productId = productId;
            this.wishlistDao = wishlistDao;
        }

        @Override
        public Wishlist call() throws Exception {
            return wishlistDao.getWishlistForUserAndProduct(userId,productId);
        }
    }


}

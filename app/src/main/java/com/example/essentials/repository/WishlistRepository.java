package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.WishlistDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.executors.AppExecutors;
import com.example.essentials.utils.APIUtils;

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
           APIUtils.getFirebaseCrashlytics().recordException(e);
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



    public void deleteWishlistItems(Wishlist wishlist) {
        AppExecutors.getInstance().diskIO().execute(() -> wishlistDao.delete(wishlist));

    }



}

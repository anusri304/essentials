package com.example.essentials.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.essentials.dao.AddressDao;
import com.example.essentials.dao.CartDao;
import com.example.essentials.dao.CategoryDao;
import com.example.essentials.dao.ProductDao;
import com.example.essentials.dao.UserDao;
import com.example.essentials.dao.WishlistDao;
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Category;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;
import com.example.essentials.domain.Wishlist;

@Database(entities = {User.class, Product.class, Wishlist.class, Cart.class, Category.class, Address.class}, version = 1, exportSchema = false)
public abstract class EssentialsRoomDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract WishlistDao wishlistDao();
    public abstract CartDao cartDao();
    public abstract CategoryDao categoryDao();
    public abstract AddressDao addressDao();
    private static final Object LOCK = new Object();

    private static volatile EssentialsRoomDatabase essentialsRoomDatabase;
    private static final String DATABASE_NAME = "essentials";

    public static EssentialsRoomDatabase getDatabase(final Context context) {
        if (essentialsRoomDatabase == null) {
            synchronized (LOCK) {
                if (essentialsRoomDatabase == null) {
                    essentialsRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            EssentialsRoomDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return essentialsRoomDatabase;
    }

}

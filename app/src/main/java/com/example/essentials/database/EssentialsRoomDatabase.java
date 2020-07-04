package com.example.essentials.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.essentials.dao.UserDao;
import com.example.essentials.domain.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class EssentialsRoomDatabase extends RoomDatabase {

    public abstract UserDao userDao();
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

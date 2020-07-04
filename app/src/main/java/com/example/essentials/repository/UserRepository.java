package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.UserDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.User;
import com.example.essentials.executors.AppExecutors;


import java.util.List;

@SuppressWarnings("ALL")
public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        userDao = db.userDao();
    }


    public void insertUser(User user){
        AppExecutors.getInstance().diskIO().execute(() -> userDao.insertUser(user));

    }

//    public LiveData<Movie> getMovie(int movieId) {
//       return  movieDao.getMovie(movieId);
//    }
}

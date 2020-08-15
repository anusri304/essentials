package com.example.essentials.repository;

import android.app.Application;

import com.example.essentials.dao.UserDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.User;
import com.example.essentials.executors.AppExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        userDao = db.userDao();
    }


    public void insertUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        AppExecutors.getInstance().diskIO().execute(() -> userDao.updateUser(user));

    }

    public User getUser(int customerId) {

//        AppExecutors.getInstance().diskIO().execute(() -> userDao.getUser(customerId));
//        return userDao.getUser(customerId);
        User user = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<User> future = executorService.submit(new MyInfoCallable(customerId, userDao));
            user = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    private static class MyInfoCallable implements Callable<User> {

        int userId;
        UserDao userDao;

        public MyInfoCallable(int userId, UserDao userDao) {
            this.userId = userId;
            this.userDao = userDao;
        }

        @Override
        public User call() throws Exception {
            return userDao.getUser(userId);
        }
    }
}

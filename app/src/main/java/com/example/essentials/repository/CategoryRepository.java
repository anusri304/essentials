package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.CategoryDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Category;
import com.example.essentials.executors.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class CategoryRepository {
    private CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
    }


    public void insertCategory(Category category) {
        AppExecutors.getInstance().diskIO().execute(() -> categoryDao.insertCategory(category));
    }

    public void updateCategory(Category category) {
        AppExecutors.getInstance().diskIO().execute(() -> categoryDao.updateCategory(category));

    }

    public Category getCategory(int categoryId) {

//        AppExecutors.getInstance().diskIO().execute(() -> userDao.getUser(customerId));
//        return userDao.getUser(customerId);
        Category category = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<Category> future = executorService.submit(new MyInfoCallable(categoryId, categoryDao));
            category = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    private static class MyInfoCallable implements Callable<Category> {

        int categoryId;
        CategoryDao categoryDao;

        public MyInfoCallable(int categoryId, CategoryDao categoryDao) {
            this.categoryId = categoryId;
            this.categoryDao = categoryDao;
        }

        @Override
        public Category call() throws Exception {
            return categoryDao.getCategory(categoryId);
        }
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }
}

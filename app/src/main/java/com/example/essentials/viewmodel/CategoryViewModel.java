package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.essentials.domain.Category;
import com.example.essentials.domain.User;
import com.example.essentials.repository.CategoryRepository;
import com.example.essentials.repository.UserRepository;

@SuppressWarnings("ALL")
public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    int categoryId;
    Category category;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
    }

    public CategoryViewModel(@NonNull Application application, int categoryId) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        category = categoryRepository.getCategory(categoryId);
    }

    public void insertCategory(Category category){
        categoryRepository.insertCategory(category);
    }


    public void updateCategory(Category category){
        categoryRepository.updateCategory(category);
    }
    public Category getCategory(int categoryId){
        return categoryRepository.getCategory(categoryId);
    }

}

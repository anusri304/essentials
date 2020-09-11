package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.Category;
import com.example.essentials.repository.CategoryRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    int categoryId;
    Category category;
    LiveData<List<Category>> categories;

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

    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

}

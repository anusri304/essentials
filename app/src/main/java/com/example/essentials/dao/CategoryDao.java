package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Delete
    void delete(Category category);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(Category category);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateCategory(Category category);

    @Query("SELECT * from Category" )
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * from Category where id=:categoryId")
    Category getCategory(int categoryId);
}

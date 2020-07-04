package com.example.essentials.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.essentials.domain.User;

@Dao
public interface UserDao {
    @Delete
    void delete(User user);

    @Insert
    void insertUser(User user);

//    @Query("SELECT * from Movie where id=:movieId")
//    LiveData<Movie> getMovie(int movieId);
}

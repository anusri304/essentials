package com.example.essentials.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.User;

@Dao
public interface UserDao {
    @Delete
    void delete(User user);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateUser(User user);

    @Query("SELECT * from User where id=:customerId")
    User getUser(int customerId);

//    @Query("SELECT * from Movie where id=:movieId")
//    LiveData<Movie> getMovie(int movieId);
}

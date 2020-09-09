package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.OrderCustomer;

import java.util.List;

@Dao
public interface OrderCustomerDao {
    @Delete
    void delete(OrderCustomer order);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertOrderCustomer(OrderCustomer order);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateOrderCustomer(OrderCustomer order);

    @Query("SELECT * from OrderCustomer ORDER BY  dateAdded DESC" )
    LiveData<List<OrderCustomer>> getAllOrderCustomer();

}

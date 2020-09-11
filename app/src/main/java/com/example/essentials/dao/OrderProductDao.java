package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.OrderProduct;

import java.util.List;

@Dao
public interface OrderProductDao {
    @Delete
    void delete(OrderProduct orderProduct);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertOrderProduct(OrderProduct orderProduct);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    void updateOrderProduct(OrderProduct orderProduct);

    @Query("SELECT * from OrderProduct" )
    LiveData<List<OrderProduct>> getAllOrderProduct();

}

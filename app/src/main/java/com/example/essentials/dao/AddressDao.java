package com.example.essentials.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.essentials.domain.Address;

import java.util.List;

@Dao
public interface AddressDao {
    @Delete
    void delete(Address address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAddress(Address address);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateAddress(Address address);

    @Query("SELECT * from Address where id=:addressId" )
    Address getAddressForId(int addressId);

    @Query("SELECT * from Address" )
    LiveData<List<Address>> getAllAddress();


}

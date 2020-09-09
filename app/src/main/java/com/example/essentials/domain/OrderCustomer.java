package com.example.essentials.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.essentials.utils.LocalDateTimeConverter;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(foreignKeys = {
        @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId"),
        @ForeignKey(entity = Address.class,
                parentColumns = "id",
                childColumns = "addressId")
})
@AllArgsConstructor
@NoArgsConstructor
public class OrderCustomer {
    @PrimaryKey
    public int id;
    @ColumnInfo(index = true)
    public int userId;
    @ColumnInfo(index = true)
    public int addressId;
    private String paymentCustomerName;
    private String status;
    private double total;
    @TypeConverters(LocalDateTimeConverter.class)
    private Date dateAdded;
}

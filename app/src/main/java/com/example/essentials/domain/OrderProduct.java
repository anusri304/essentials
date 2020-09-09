package com.example.essentials.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(foreignKeys = {
        @ForeignKey(entity = OrderCustomer.class,
                parentColumns = "id",
                childColumns = "orderId"),
        @ForeignKey(entity = Product.class,
                parentColumns = "id",
                childColumns = "productId")

})
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(index = true)
    public int orderId;
    @ColumnInfo(index = true)
    public int productId;
    public String productName;
    public int quantity;
    public double total;
    public double price;
    public String productImage;
}

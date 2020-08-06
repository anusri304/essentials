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
        @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId"),
        @ForeignKey(entity = Product.class,
                parentColumns = "id",
                childColumns = "productId")
})
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(index = true)
    public int userId;
    @ColumnInfo(index = true)
    public int productId;
    public int quantity;
}

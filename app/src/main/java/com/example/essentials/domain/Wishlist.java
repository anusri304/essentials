package com.example.essentials.domain;

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
public class Wishlist {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int productId;
}

package com.example.essentials.domain;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(foreignKeys = {
        @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId")
})
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @PrimaryKey
    public int id;
    public int categoryId;
    public String name;
    public String imagePath;
    public String description;
    public String price;
    public String special;
    public String discPerc;
    public String inStock;
}

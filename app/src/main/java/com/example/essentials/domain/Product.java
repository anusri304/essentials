package com.example.essentials.domain;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @PrimaryKey
    public int id;
    public String name;
    public String imagePath;
    public String description;
    public String price;
    public String special;
    public String discPerc;
    public String inStock;
}

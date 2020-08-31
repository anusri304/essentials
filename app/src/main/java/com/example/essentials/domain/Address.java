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
                childColumns = "userId")

})
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(index = true)
    public int userId;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
    private String postalCode;
}

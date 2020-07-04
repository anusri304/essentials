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
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String mobileNumber;
    private String password;
}

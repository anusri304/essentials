package com.example.essentials.activity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInputBean {
    String username;
    String key;
    String loginUser;
    String password;
}

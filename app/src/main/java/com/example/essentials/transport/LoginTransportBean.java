package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTransportBean {
    String message;
    @JsonRequired
    String apiToken;
    @JsonRequired
    String customerId;
    @JsonRequired
    String firstname;
    @JsonRequired
    String lastname;
    @JsonRequired
    String email;
    @JsonRequired
    String telephone;
}

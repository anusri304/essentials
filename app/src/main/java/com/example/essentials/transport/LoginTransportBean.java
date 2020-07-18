package com.example.essentials.transport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTransportBean {
    String message;
    String apiToken;
    String customerId;
    String firstname;
    String lastname;
    String email;
    String telephone;
}

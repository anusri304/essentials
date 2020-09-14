package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistTransportBean {
    String message;
    @JsonRequired
    String total;
}

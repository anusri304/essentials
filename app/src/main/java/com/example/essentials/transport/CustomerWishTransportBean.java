package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWishTransportBean {
    @JsonRequired
    String productId;
}

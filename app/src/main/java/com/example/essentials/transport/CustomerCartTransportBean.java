package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCartTransportBean {
    @JsonRequired
    String productId;
    @JsonRequired
    String quantity;
}

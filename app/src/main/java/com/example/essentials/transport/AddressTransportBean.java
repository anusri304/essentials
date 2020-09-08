package com.example.essentials.transport;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressTransportBean {
    @SerializedName("address_id")
    int id;
    String message;
}

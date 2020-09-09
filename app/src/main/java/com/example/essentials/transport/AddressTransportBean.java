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
    String addressId;
    String firstname;
    String lastname;
    @SerializedName("address_1")
    String address1;
    @SerializedName("address_2")
    String address2;
    String postcode;
    String city;
    String country;
}

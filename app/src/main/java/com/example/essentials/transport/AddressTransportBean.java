package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressTransportBean {
    @JsonRequired
    @SerializedName("addressId")
    String addressId;
    @JsonRequired
    String firstname;
    @JsonRequired
    String lastname;
    @JsonRequired
    @SerializedName("address_1")
    String address1;
    @JsonRequired
    @SerializedName("address_2")
    String address2;
    @JsonRequired
    String postcode;
    @JsonRequired
    String city;
    @JsonRequired
    String country;
}

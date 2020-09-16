package com.example.essentials.transport;

import com.example.essentials.annotation.JsonRequired;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressListTransportBean {
    @SerializedName("address_id")
    int id;
    String message;
    @SerializedName("address")
    List<AddressTransportBean> address;
}

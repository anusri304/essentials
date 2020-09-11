package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.essentials.domain.Address;
import com.example.essentials.repository.AddressRepository;

import java.util.List;

public class AddressViewModel extends AndroidViewModel {
    private AddressRepository addressRepository;
    int cartId;
    Address address;
    LiveData<List<Address>> addressList;
    private MutableLiveData<Integer> quantity;

    public AddressViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
        addressList = addressRepository.getAllAddress();
    }

    public MutableLiveData<Integer> getQuantity() {
        if (quantity == null) {
            quantity = new MutableLiveData<Integer>();
        }
        return quantity;
    }

    public AddressViewModel(@NonNull Application application, int addressId) {
        super(application);
        addressRepository = new AddressRepository(application);
        address = addressRepository.getAddressForId(addressId);
        addressList = addressRepository.getAllAddress();
    }

    public void insertAddress(Address address){
        addressRepository.insertAddress(address);
    }

    public void updateAddress(Address address){
        addressRepository.updateAddress(address);
    }

    public void deleteAddress(Address address){
        addressRepository.deleteAddress(address);
    }



    public Address getAddressForId(int addressId){
        return addressRepository.getAddressForId(addressId);
    }

    public LiveData<List<Address>> getAllAddress() {
        return addressList;
    }

}

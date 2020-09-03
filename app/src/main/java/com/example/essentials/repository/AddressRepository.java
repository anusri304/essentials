package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.AddressDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Address;
import com.example.essentials.executors.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AddressRepository {
    private AddressDao addressDao;

    public AddressRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        addressDao = db.addressDao();
    }

    public void insertAddress(Address address) {
        AppExecutors.getInstance().diskIO().execute(() -> addressDao.insertAddress(address));
    }

    public void updateAddress(Address address) {
        AppExecutors.getInstance().diskIO().execute(() -> addressDao.updateAddress(address));

    }

    public void deleteAddress(Address address) {
        AppExecutors.getInstance().diskIO().execute(() -> addressDao.delete(address));

    }


    public Address getAddressForId(int addressId) {
        Address address = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<Address> future = executorService.submit(new AddressRepository.MyInfoCallable(addressId, addressDao));
            address = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public LiveData<List<Address>> getAllAddress() {
        return addressDao.getAllAddress();
    }

    private static class MyInfoCallable implements Callable<Address> {

        int addressId;
        AddressDao addressDao;

        public MyInfoCallable(int addressId, AddressDao addressDao) {
            this.addressId = addressId;
            this.addressDao = addressDao;
        }

        @Override
        public Address call() throws Exception {
            return addressDao.getAddressForId(addressId);
        }
    }
}

package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.domain.User;
import com.example.essentials.repository.OrderCustomerRepository;
import com.example.essentials.repository.UserRepository;

@SuppressWarnings("ALL")
public class OrderCustomerViewModel extends AndroidViewModel {
    private OrderCustomerRepository orderCustomerRepository;

    public OrderCustomerViewModel(@NonNull Application application) {
        super(application);
        orderCustomerRepository = new OrderCustomerRepository(application);
    }

    public OrderCustomerViewModel(@NonNull Application application, int userId) {
        super(application);
        orderCustomerRepository = new OrderCustomerRepository(application);
    }

    public void insertOrderCustomer(OrderCustomer orderCustomer){
        orderCustomerRepository.insertOrderCustomer(orderCustomer);
    }


    public void updateOrderCustomer(OrderCustomer orderCustomer){
        orderCustomerRepository.updateOrderCustomer(orderCustomer);
    }

}

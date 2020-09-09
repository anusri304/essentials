package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.domain.User;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.repository.OrderCustomerRepository;
import com.example.essentials.repository.UserRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class OrderCustomerViewModel extends AndroidViewModel {
    private OrderCustomerRepository orderCustomerRepository;

    LiveData<List<OrderCustomer>> orderCustomers;

    public OrderCustomerViewModel(@NonNull Application application) {
        super(application);
        orderCustomerRepository = new OrderCustomerRepository(application);
        orderCustomers = orderCustomerRepository.getAllOrderCustomer();
//        System.out.println("orderCustomers"+orderCustomers.getValue().size());
    }

    public OrderCustomerViewModel(@NonNull Application application, int userId) {
        super(application);
        orderCustomerRepository = new OrderCustomerRepository(application);
        orderCustomers = orderCustomerRepository.getAllOrderCustomer();
    }

    public void insertOrderCustomer(OrderCustomer orderCustomer){
        orderCustomerRepository.insertOrderCustomer(orderCustomer);
    }


    public void updateOrderCustomer(OrderCustomer orderCustomer){
        orderCustomerRepository.updateOrderCustomer(orderCustomer);
    }

    public LiveData<List<OrderCustomer>> getAllOrdercustomer() {
        return orderCustomers;
    }

}

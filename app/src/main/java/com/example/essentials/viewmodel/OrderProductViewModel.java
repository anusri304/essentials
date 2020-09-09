package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.domain.OrderProduct;
import com.example.essentials.repository.OrderCustomerRepository;
import com.example.essentials.repository.OrderProductRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class OrderProductViewModel extends AndroidViewModel {
    private OrderProductRepository orderProductRepository;
    LiveData<List<OrderProduct>> orderProducts;

    public OrderProductViewModel(@NonNull Application application) {
        super(application);
        orderProductRepository = new OrderProductRepository(application);
        orderProducts = orderProductRepository.getAllOrderProduct();
    }

    public OrderProductViewModel(@NonNull Application application, int userId) {
        super(application);
        orderProductRepository = new OrderProductRepository(application);
        orderProducts = orderProductRepository.getAllOrderProduct();
    }

    public void insertOrderProduct(OrderProduct orderProduct){
        orderProductRepository.insertOrderProduct(orderProduct);
    }


    public void updateOrderProduct(OrderProduct orderProduct){
        orderProductRepository.updateOrderProduct(orderProduct);
    }

    public  LiveData<List<OrderProduct>>  getAllOrderProducts(){
        return orderProducts;
    }

}

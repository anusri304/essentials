package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.OrderCustomerDao;
import com.example.essentials.dao.OrderProductDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.domain.OrderProduct;
import com.example.essentials.executors.AppExecutors;

import java.util.List;

@SuppressWarnings("ALL")
public class OrderProductRepository {
    private OrderProductDao orderProductDao;

    public OrderProductRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        orderProductDao = db.orderProductDao();
    }
    public void insertOrderProduct(OrderProduct orderProduct) {
        AppExecutors.getInstance().diskIO().execute(() -> orderProductDao.insertOrderProduct(orderProduct));
    }

    public void updateOrderProduct(OrderProduct orderProduct) {
        AppExecutors.getInstance().diskIO().execute(() -> orderProductDao.updateOrderProduct(orderProduct));

    }

    public void deleteOrderProduct(OrderProduct orderProduct) {
        AppExecutors.getInstance().diskIO().execute(() -> orderProductDao.delete(orderProduct));

    }

    public LiveData<List<OrderProduct>> getAllOrderProduct() {
        return orderProductDao.getAllOrderProduct();
    }

}

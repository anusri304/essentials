package com.example.essentials.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.CartDao;
import com.example.essentials.dao.OrderCustomerDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.executors.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class OrderCustomerRepository {
    private OrderCustomerDao orderCustomerDao;

    public OrderCustomerRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        orderCustomerDao = db.orderCustomerDao();
    }
    public void insertOrderCustomer(OrderCustomer orderCustomer) {
        AppExecutors.getInstance().diskIO().execute(() -> orderCustomerDao.insertOrderCustomer(orderCustomer));
    }

    public void updateOrderCustomer(OrderCustomer orderCustomer) {
        AppExecutors.getInstance().diskIO().execute(() -> orderCustomerDao.updateOrderCustomer(orderCustomer));

    }

    public void deleteOrderCustomer(OrderCustomer orderCustomer) {
        AppExecutors.getInstance().diskIO().execute(() -> orderCustomerDao.delete(orderCustomer));

    }

    public LiveData<List<OrderCustomer>> getAllOrderCustomer() {
        return orderCustomerDao.getAllOrderCustomer();
    }

}

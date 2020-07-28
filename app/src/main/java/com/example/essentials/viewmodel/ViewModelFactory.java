package com.example.essentials.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// COMPLETED (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // COMPLETED (2) Add two member variables. One for the database and one for the taskId
    private final Application application;


    // COMPLETED (3) Initialize the member variables in the constructor with the parameters received
    public ViewModelFactory(Application mdB) {
        application = mdB;
    }

    // COMPLETED (4) Uncomment the following method
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
       if(modelClass.getName().contains("UserViewModel")) {
           return (T) new UserViewModel(application);
       }
       else if(modelClass.getName().contains("ProductViewModel")) {
           return (T) new ProductViewModel(application);
       }
       else return null;
    }
}

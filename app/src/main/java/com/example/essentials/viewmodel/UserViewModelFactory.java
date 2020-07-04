package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// COMPLETED (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory
public class UserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // COMPLETED (2) Add two member variables. One for the database and one for the taskId
    private final Application application;

    private final int mUserId;

    // COMPLETED (3) Initialize the member variables in the constructor with the parameters received
    public UserViewModelFactory(Application mdB, int userId) {
        application = mdB;
        mUserId= userId;
    }

    // COMPLETED (4) Uncomment the following method
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new UserViewModel(application,mUserId);
    }
}

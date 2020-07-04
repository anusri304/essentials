package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.essentials.domain.User;
import com.example.essentials.repository.UserRepository;

@SuppressWarnings("ALL")
public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    int userId;
    User user;

    public UserViewModel(@NonNull Application application, int mUserId) {
        userRepository = new UserRepository(application);
        userId = mUserId;
    }

    public void insertUser(User user){
        userRepository.insertUser(user);
    }

}

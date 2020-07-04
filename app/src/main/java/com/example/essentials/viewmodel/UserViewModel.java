package com.example.essentials.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.essentials.domain.User;
import com.example.essentials.repository.UserRepository;

@SuppressWarnings("ALL")
public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    int userId;
    User user;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void insertUser(User user){
        userRepository.insertUser(user);
    }

}

package com.example.essentials.utils;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication sApplication;

    public static MyApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = MyApplication.this;
    }

}
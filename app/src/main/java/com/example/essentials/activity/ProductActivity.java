package com.example.essentials.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.essentials.R;
import com.example.essentials.databinding.ActivityLoginBinding;
import com.example.essentials.databinding.ActivityProductBinding;

public class ProductActivity extends AppCompatActivity {
    ActivityProductBinding activityProductBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductBinding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        setSupportActionBar(activityProductBinding.toolbar);
        activityProductBinding.toolbarTitle.setText(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}

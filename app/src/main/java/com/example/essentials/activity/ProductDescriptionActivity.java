package com.example.essentials.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.essentials.R;
import com.example.essentials.utils.ApplicationConstants;

public class ProductDescriptionActivity extends AppCompatActivity {
    String productDesc;
    String productName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_desc);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            productDesc = getIntent().getStringExtra(ApplicationConstants.PRODUCT_DESC);
            productName = getIntent().getStringExtra(ApplicationConstants.PRODUCT_NAME);
            setTitle(productName);
        }
        initLayout();
    }

    private void initLayout() {
        TextView productDescTxtView = findViewById(R.id.prod_desc_textView);
        productDescTxtView.setText(productDesc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

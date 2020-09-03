package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Product;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryAddressActivity extends AppCompatActivity {
    String TAG = "DeliveryAddressFragment";
    View rootView;
    ImageView addImage;
    List<Address> address = new ArrayList<>();
    AddressViewModel addressViewModel;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.delivery_address));
        setContentView(R.layout.activity_address);
        addImage = (ImageView) findViewById(R.id.add_delivery);
        ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
        addressViewModel = new ViewModelProvider(this, factory).get(AddressViewModel.class);
        relativeLayout = findViewById(R.id.relativeLayout);
        initImageView();
        observeChanges();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initImageView() {
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryAddressActivity.this, AddDeliveryAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void observeChanges() {
        addressViewModel.getAllAddress().observe(this, objAddress -> {
            address = objAddress;

            if (!address.isEmpty()) {
                address.stream().forEach(address1 -> createCardView(relativeLayout,address1));
                ;
            }
        });
    }

    private void createCardView(RelativeLayout relativeLayout,Address address) {
        CardView cardview = new CardView(getApplicationContext());
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutparams.addRule(RelativeLayout.BELOW, R.id.addressCardView);
        cardview.setLayoutParams(layoutparams);
        cardview.setRadius(15);

        cardview.setPadding(25, 25, 25, 25);

        cardview.setCardBackgroundColor(getResources().getColor(R.color.gray));

        cardview.setMaxCardElevation(30);

        cardview.setMaxCardElevation(6);

        TextView textview = new TextView(getApplicationContext());

        textview.setLayoutParams(layoutparams);

        textview.setText(TextUtils.concat(address.getFirstName(),"\n",address.getLastName(),"\n",address.getAddressLine1(),"\n",address.getAddressLine2()));

        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

        textview.setTextColor(getResources().getColor(R.color.colorPrimary));

        textview.setPadding(25,25,25,25);

        textview.setGravity(Gravity.LEFT);

        cardview.addView(textview);

        relativeLayout.addView(cardview);
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

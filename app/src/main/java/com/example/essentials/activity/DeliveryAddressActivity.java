package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.AddressPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.AddressRecyclerViewAdapter;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Product;
import com.example.essentials.service.AddressService;
import com.example.essentials.transport.AddressTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryAddressActivity extends AppCompatActivity implements AddressRecyclerViewAdapter.ListItemClickListener {
    String TAG = "DeliveryAddressFragment";
    View rootView;
    ImageView addImage;
    static List<Address> address = new ArrayList<>();
    AddressViewModel addressViewModel;
    RelativeLayout relativeLayout;
    CardView addressCardView;
    AddressRecyclerViewAdapter addressRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.delivery_address));
        setContentView(R.layout.activity_address);
        addImage = (ImageView) findViewById(R.id.add_delivery);
        ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
        addressViewModel = new ViewModelProvider(this, factory).get(AddressViewModel.class);
        relativeLayout = findViewById(R.id.relativeLayout);
        addressCardView = findViewById(R.id.addressCardView);
        initImageView();
        observeChanges();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initImageView() {
        addressCardView.setOnClickListener(new View.OnClickListener() {
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


            addressRecyclerViewAdapter = new AddressRecyclerViewAdapter(getApplicationContext(), EssentialsUtils.getAddressPresentationBeans(address), this);
            RecyclerView recyclerView = findViewById(R.id.rv_address);
            recyclerView.setAdapter(addressRecyclerViewAdapter);

            GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), EssentialsUtils.getSpan(getApplicationContext()));
            recyclerView.setLayoutManager(manager);

        });
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

    @Override
    public void onListItemClick(AddressPresentationBean addressPresentationBean) {
        callDeleteEndpoint(addressPresentationBean.getId());

    }

    private void callDeleteEndpoint(int addressId) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        AddressService addressService = APIUtils.getRetrofit().create(AddressService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("customerId", String.valueOf(APIUtils.getLoggedInUserId(DeliveryAddressActivity.this)))
                .addFormDataPart("addressId",  String.valueOf(addressId))
                .build();
        Call<AddressTransportBean> call = addressService.deleteAddress(apiToken, requestBody);

        call.enqueue(new Callback<AddressTransportBean>() {
            @Override
            public void onResponse(Call<AddressTransportBean> call, Response<AddressTransportBean> response) {
                AddressTransportBean addressTransportBean = response.body();
                if (response.isSuccessful() && addressTransportBean.getMessage() != null && addressTransportBean.getMessage().contains(ApplicationConstants.SUCCESS)) {
                    //   EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.LOGIN_SUCCESS);
                    Address address = addressViewModel.getAddressForId(addressId);
                    if (address != null) {
                        addressViewModel.deleteAddress(address);
                    }

                }
            }

            @Override
            public void onFailure(Call<AddressTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });
    }
}

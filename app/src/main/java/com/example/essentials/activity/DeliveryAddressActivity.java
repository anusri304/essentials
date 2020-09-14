package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.AddressPresentationBean;
import com.example.essentials.adapter.AddressRecyclerViewAdapter;
import com.example.essentials.domain.Address;
import com.example.essentials.fragment.ProductFragment;
import com.example.essentials.service.AddressService;
import com.example.essentials.transport.AddressListTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryAddressActivity extends AppCompatActivity implements AddressRecyclerViewAdapter.ListItemClickListener {
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
        AddressService addressService = RetrofitUtils.getRetrofitForAddress().create(AddressService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("customerId", String.valueOf(APIUtils.getLoggedInUserId(DeliveryAddressActivity.this)))
                .addFormDataPart("addressId",  String.valueOf(addressId))
                .build();
        Call<AddressListTransportBean> call = addressService.deleteAddress(apiToken, requestBody);

        call.enqueue(new Callback<AddressListTransportBean>() {
            @Override
            public void onResponse(Call<AddressListTransportBean> call, Response<AddressListTransportBean> response) {
                AddressListTransportBean addressListTransportBean = response.body();
                if (response.isSuccessful() && addressListTransportBean.getMessage() != null && addressListTransportBean.getMessage().contains(ApplicationConstants.SUCCESS)) {
                    //   EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.LOGIN_SUCCESS);
                    Address address = addressViewModel.getAddressForId(addressId);
                    APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.ADDRESS_ID,addressId);
                    if (address != null) {
                        addressViewModel.deleteAddress(address);
                    }

                }
            }

            @Override
            public void onFailure(Call<AddressListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(DeliveryAddressActivity.class.getName().concat( " ").concat(throwable.getMessage()));
            }
        });
    }
}

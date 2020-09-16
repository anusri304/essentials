package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.domain.Address;
import com.example.essentials.service.AddressService;
import com.example.essentials.transport.AddressListTransportBean;
import com.example.essentials.transport.AddressTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    RecyclerView recyclerView;
    List<AddressPresentationBean> addressPresentationBeans = new ArrayList<AddressPresentationBean>();
    String customerDetails="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkUtils.isNetworkConnected(DeliveryAddressActivity.this)) {
            EssentialsUtils.showAlertDialog(DeliveryAddressActivity.this, ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
        }
        else {
            if(getIntent()!=null && getIntent().getStringExtra(ApplicationConstants.CUSTOMER_DETAILS)!=null){
                customerDetails= getIntent().getStringExtra(ApplicationConstants.CUSTOMER_DETAILS);
            }
            else {
                customerDetails= ApplicationConstants.NO;
            }
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

            addressPresentationBeans = EssentialsUtils.getAddressPresentationBeans(address);
            setData(addressPresentationBeans);

        });
    }

    private void setData(List<AddressPresentationBean> addressPresentationBeans) {
        addressRecyclerViewAdapter = new AddressRecyclerViewAdapter(getApplicationContext(), EssentialsUtils.getAddressPresentationBeans(address), this,customerDetails);
        recyclerView = findViewById(R.id.rv_address);
        recyclerView.setAdapter(addressRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), EssentialsUtils.getSpan(getApplicationContext()));
        recyclerView.setLayoutManager(manager);
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AddressTransportBean.class, new AnnotatedDeserializer<AddressTransportBean>())
                .setLenient().create();
        AddressService addressService = RetrofitUtils.getRetrofit(gson).create(AddressService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(ApplicationConstants.CUSTOMER_ID, String.valueOf(APIUtils.getLoggedInUserId(DeliveryAddressActivity.this)))
                .addFormDataPart(ApplicationConstants.ADDRESS_ID,  String.valueOf(addressId))
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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        if (recyclerView != null && addressPresentationBeans != null) {
            Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            // putting recyclerview position
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
            // putting recyclerview items
            savedInstanceState.putParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(addressPresentationBeans));
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID);
        // getting recyclerview items
        addressPresentationBeans = savedInstanceState.getParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID);
        // Restoring adapter items
        if (recyclerView != null && addressPresentationBeans != null) {
            setData(addressPresentationBeans);
            // Restoring recycler view position
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }
}

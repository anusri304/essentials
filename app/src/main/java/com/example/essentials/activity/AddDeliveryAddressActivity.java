package com.example.essentials.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.essentials.R;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.databinding.ActivityAddAddressBinding;
import com.example.essentials.domain.Address;
import com.example.essentials.service.AddressService;
import com.example.essentials.transport.AddressListTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDeliveryAddressActivity extends AppCompatActivity {
    View rootView;
    ActivityAddAddressBinding activityAddAddressBinding;
   AddressViewModel addressViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkUtils.isNetworkConnected(AddDeliveryAddressActivity.this)) {
            EssentialsUtils.showAlertDialog(AddDeliveryAddressActivity.this, ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
        }
        else {
            setTitle(getString(R.string.add_delivery_address));
            activityAddAddressBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_address);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
            addressViewModel = new ViewModelProvider(this, factory).get(AddressViewModel.class);
            initSaveButton();
        }
    }

    private void initSaveButton() {
        activityAddAddressBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()) {
                    callAddressEndpoint();
                }
            }
        });
    }

    private void saveAddress(int id) {
            Address address = new Address();
            address.setId(id);
            address.setUserId(APIUtils.getLoggedInUserId(AddDeliveryAddressActivity.this));
            address.setFirstName(activityAddAddressBinding.editTextFirstname.getText().toString());
            address.setLastName(activityAddAddressBinding.editTextLastname.getText().toString());
            address.setAddressLine1(activityAddAddressBinding.editTextAddress1.getText().toString());
            address.setAddressLine2(activityAddAddressBinding.editTextAddress2.getText().toString());
            address.setPostalCode(activityAddAddressBinding.editTextPostalCode.getText().toString());
            address.setCity(activityAddAddressBinding.editTextCity.getText().toString());
            address.setCountry(activityAddAddressBinding.editTextCountry.getText().toString());
            addressViewModel.insertAddress(address);
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

    private boolean validateFields() {
        boolean isValid = true;

        if (activityAddAddressBinding.editTextFirstname.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutFirstname.setError(ApplicationConstants.FIRST_NAME_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutFirstname.setError(null);
        }

        if (activityAddAddressBinding.editTextLastname.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutLastname.setError(ApplicationConstants.LAST_NAME_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutLastname.setError(null);
        }

        if (activityAddAddressBinding.editTextAddress1.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutAddress1.setError(ApplicationConstants.ADDRESS1_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutAddress1.setError(null);
        }

        if (activityAddAddressBinding.editTextAddress2.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutAddress2.setError(ApplicationConstants.ADDRESS2_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutAddress2.setError(null);
        }

        if (activityAddAddressBinding.editTextCity.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutCity.setError(ApplicationConstants.CITY_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutCity.setError(null);
        }

        if (activityAddAddressBinding.editTextPostalCode.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutPostalCode.setError(ApplicationConstants.POSTAL_CODE_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutPostalCode.setError(null);
        }
        if (activityAddAddressBinding.editTextCountry.getText().toString().isEmpty()) {
            isValid = false;
            activityAddAddressBinding.textInputLayoutCountry.setError(ApplicationConstants.COUNTRY_ERROR_MESSAGE);
        } else {
            activityAddAddressBinding.textInputLayoutCountry.setError(null);
        }
        return isValid;
    }

    public void callAddressEndpoint(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AddressListTransportBean.class, new AnnotatedDeserializer<AddressListTransportBean>())
                .setLenient().create();
        AddressService addressService = RetrofitUtils.getRetrofit(gson).create(AddressService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(ApplicationConstants.FIRST_NAME, activityAddAddressBinding.editTextFirstname.getText().toString())
                .addFormDataPart(ApplicationConstants.CUSTOMER_ID, String.valueOf(APIUtils.getLoggedInUserId(AddDeliveryAddressActivity.this)))
                .addFormDataPart(ApplicationConstants.LAST_NAME, activityAddAddressBinding.editTextLastname.getText().toString())
                .addFormDataPart(ApplicationConstants.ADDRESS_1, activityAddAddressBinding.editTextAddress1.getText().toString())
                .addFormDataPart(ApplicationConstants.ADDRESS_2, activityAddAddressBinding.editTextAddress2.getText().toString())
                .addFormDataPart(ApplicationConstants.POSTCODE, activityAddAddressBinding.editTextPostalCode.getText().toString())
                .addFormDataPart(ApplicationConstants.CITY, activityAddAddressBinding.editTextCity.getText().toString())
                .addFormDataPart(ApplicationConstants.ZONE_ID, ApplicationConstants.ZONE_ID_VALUE)
                .addFormDataPart(ApplicationConstants.COUNTRY_ID, ApplicationConstants.COUNTRY_ID_VALUE)
                .build();
        Call<AddressListTransportBean> call = addressService.addAddress(apiToken,requestBody);

        call.enqueue(new Callback<AddressListTransportBean>() {
            @Override
            public void onResponse(Call<AddressListTransportBean> call, Response<AddressListTransportBean> response) {
                AddressListTransportBean addressListTransportBean = response.body();
                if (response.isSuccessful() && addressListTransportBean.getMessage() != null && addressListTransportBean.getMessage().contains(ApplicationConstants.SUCCESS)) {
                 //   EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.LOGIN_SUCCESS);
                    saveAddress(addressListTransportBean.getId());
                    Intent intent = new Intent(AddDeliveryAddressActivity.this, DeliveryAddressActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<AddressListTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(AddDeliveryAddressActivity.class.getName().concat( " ").concat(throwable.getMessage()));
            }
        });
    }
}

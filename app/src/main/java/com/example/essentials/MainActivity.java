package com.example.essentials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.essentials.databinding.ActivityRegisterBinding;
import com.example.essentials.service.CustomerService;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.utils.ApplicationConstants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.databinding.DataBindingUtil;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Main Activity";
    private static Retrofit retrofit = null;
    private AppCompatButton registerButton;
    ActivityRegisterBinding activityRegisterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        activityRegisterBinding.registerButton.setOnClickListener(this);
        setTitle("Register");
        //TODO: validate email address, all fields are captured
        // add newsletters, first and last name
        // network connection and rotation
    }


    private void getCustomerByEmail(View view) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
// The App will not crash for malformed JSON.
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        CustomerService customerService = retrofit.create(CustomerService.class);
        Call<RegisterTransportBean> call = customerService.registerCustomer(activityRegisterBinding.editTextEmailAddress.getText().toString(), activityRegisterBinding.editTextName.getText().toString(), activityRegisterBinding.editTextName.getText().toString(), activityRegisterBinding.editTextMobileNo.getText().toString(), activityRegisterBinding.editTextPassword.getText().toString());


        activityRegisterBinding.progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<RegisterTransportBean>() {
            @Override
            public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                RegisterTransportBean registerTransportBean = response.body();
                Log.i(TAG, "onResponse: " + registerTransportBean.getMessage());
                if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.MODIFIED_SUCCESS)) {
                    showMessage(ApplicationConstants.REGISTER_SUCCESS);
                } else {
                    showMessage(ApplicationConstants.REGISTER_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<RegisterTransportBean> call, Throwable throwable) {
                activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });
    }

    @Override
    public void onClick(View view) {
        getCustomerByEmail(view);
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(activityRegisterBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(ApplicationConstants.LOGIN, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Snackbar.make(activityRegisterBinding.coordinatorLayout, "Message successfully deleted12.", Snackbar.LENGTH_SHORT).show();

                    }
                });
        snackbar.show();
    }
}

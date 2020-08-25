package com.example.essentials.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.essentials.R;
import com.example.essentials.databinding.ActivityLoginBinding;
import com.example.essentials.domain.User;
import com.example.essentials.service.LoginCustomerService;
import com.example.essentials.transport.LoginTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login Activity";
    private static Retrofit retrofit = null;
    ActivityLoginBinding activityLoginBinding;
    UserViewModel userViewModel;
    SharedPreferences sharedpreferences;
    public static final String mypreference = ApplicationConstants.SHARED_PREF_NAME;
    public static final String userId = "userId";
    public static final String apiToken = "apiToken";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.login_title));
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!NetworkUtils.isNetworkConnected(this)) {
            EssentialsUtils.showNetworkAlertDialog(LoginActivity.this);
        } else {
            activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
            initLayout();
            if (getIntent() != null && getIntent().getStringExtra(ApplicationConstants.DISPLAY_TOAST) != null) {
                if (getIntent().getStringExtra(ApplicationConstants.DISPLAY_TOAST).equalsIgnoreCase(ApplicationConstants.REGISTER_SUCCESS)) {
                    EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.REGISTER_SUCCESS);
                }
            }
            ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
            userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        }

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

    private void initLayout() {
        activityLoginBinding.registerHereTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        activityLoginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCustomer();
            }
        });

        activityLoginBinding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
                else if (!EssentialsUtils.isValidEmail(s.toString())) {
                    activityLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE);
                } else
                    activityLoginBinding.textInputLayoutEmailAdds.setError(null);

            }
        });

        activityLoginBinding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityLoginBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
                else
                    activityLoginBinding.textInputLayoutPassword.setError(null);

            }
        });

    }



    private void loginCustomer() {
        LoginCustomerService loginCustomerService = APIUtils.getRetrofit().create(LoginCustomerService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", ApplicationConstants.API_USER)
                .addFormDataPart("key", ApplicationConstants.API_KEY)
                .addFormDataPart("loginUser", activityLoginBinding.editTextEmailAddress.getText().toString())
                .addFormDataPart("password", activityLoginBinding.editTextPassword.getText().toString())
                .build();
        Call<LoginTransportBean> call = loginCustomerService.loginCustomer(requestBody);
        activityLoginBinding.progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<LoginTransportBean>() {
            @Override
            public void onResponse(Call<LoginTransportBean> call, Response<LoginTransportBean> response) {
                LoginTransportBean loginTransportBean = response.body();
                activityLoginBinding.progressBar.setVisibility(View.INVISIBLE);
                Log.i(TAG, "onResponse: " + loginTransportBean.getMessage());
                if (loginTransportBean.getMessage() != null && loginTransportBean.getMessage().contains(ApplicationConstants.LOGIN_SUCCESS)) {
                    EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.LOGIN_SUCCESS);
                    User user = userViewModel.getUser(Integer.valueOf(loginTransportBean.getCustomerId()));
                    // if the user has registered in website the user will  be in null
                    if (user != null) {
                        user.setApiToken(loginTransportBean.getApiToken());
                        userViewModel.updateUser(user);
                    } else {
                        user = new User();
                        user.setId(Integer.valueOf(loginTransportBean.getCustomerId()));
                        user.setApiToken(loginTransportBean.getApiToken());
                        user.setEmailAddress(loginTransportBean.getEmail());
                        user.setFirstName(loginTransportBean.getFirstname());
                        user.setLastName(loginTransportBean.getLastname());
                        user.setMobileNumber(loginTransportBean.getTelephone());
                        user.setPassword(activityLoginBinding.editTextPassword.getText().toString());
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt(ApplicationConstants.USER_ID, user.getId());
                        editor.putString(ApplicationConstants.API_TOKEN, user.getApiToken());
                        editor.commit();
                        userViewModel.insertUser(user);
                    }
                    Intent intent = new Intent(LoginActivity.this, ProductActivity.class);
                    startActivity(intent);

                } else {
                    EssentialsUtils.hideKeyboard(getApplicationContext());
                    EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, loginTransportBean.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginTransportBean> call, Throwable throwable) {
                activityLoginBinding.progressBar.setVisibility(View.INVISIBLE);
                EssentialsUtils.showMessage(activityLoginBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
//                    Log.e(this.getClass().getName(), throwable.toString());
            }
        });
    }


    private boolean validateFields() {
        boolean isValid = true;

        if (activityLoginBinding.editTextEmailAddress.getText().toString().isEmpty()) {
            isValid = false;
            activityLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
        } else {
            activityLoginBinding.textInputLayoutEmailAdds.setError(null);
        }

        if (activityLoginBinding.editTextPassword.getText().toString().isEmpty()) {
            isValid = false;
            activityLoginBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
        } else {
            activityLoginBinding.textInputLayoutPassword.setError(null);
        }
        return isValid;
    }

}

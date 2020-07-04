package com.example.essentials;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.essentials.databinding.ActivityRegisterBinding;
import com.example.essentials.domain.User;
import com.example.essentials.service.CustomerService;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.UserViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Main Activity";
    private static Retrofit retrofit = null;
    private AppCompatButton registerButton;
    ActivityRegisterBinding activityRegisterBinding;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        activityRegisterBinding.registerButton.setOnClickListener(this);
        setTitle("Register");
        initLayout();
        //TODO: validate email address, all fields are captured
        // add newsletters, first and last name
        // network connection and rotation

        UserViewModelFactory factory = new UserViewModelFactory((Application) getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void initLayout() {
        activityRegisterBinding.editTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutFirstName.setError(ApplicationConstants.FIRST_NAME_ERROR_MESSAGE);
                else
                    activityRegisterBinding.textInputLayoutFirstName.setError(null);

            }
        });

        activityRegisterBinding.editTextLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutLastName.setError(ApplicationConstants.LAST_NAME_ERROR_MESSAGE);
                else
                    activityRegisterBinding.textInputLayoutLastName.setError(null);

            }
        });


        activityRegisterBinding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
                else if (!isValidEmail(s.toString())) {
                    activityRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE);
                } else
                    activityRegisterBinding.textInputLayoutEmailAdds.setError(null);

            }
        });

        activityRegisterBinding.editTextMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutMobileNo.setError(ApplicationConstants.TELEPHONE_ERROR_MESSAGE);
                else
                    activityRegisterBinding.textInputLayoutMobileNo.setError(null);

            }
        });

        activityRegisterBinding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
                else
                    activityRegisterBinding.textInputLayoutPassword.setError(null);

            }
        });

        activityRegisterBinding.editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    activityRegisterBinding.textInputLayoutConfirmPwd.setError(ApplicationConstants.CONFIRM_PASSWORD_ERROR_MESSAGE);
                else
                    activityRegisterBinding.textInputLayoutConfirmPwd.setError(null);

            }
        });

    }


    private void getCustomerByEmail(View view) {
        if (validateFields()) {
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
            Call<RegisterTransportBean> call = customerService.registerCustomer(activityRegisterBinding.editTextEmailAddress.getText().toString(), activityRegisterBinding.editTextFirstName.getText().toString(), activityRegisterBinding.editTextLastName.getText().toString(), activityRegisterBinding.editTextMobileNo.getText().toString(), activityRegisterBinding.editTextPassword.getText().toString());


            activityRegisterBinding.progressBar.setVisibility(View.VISIBLE);

            call.enqueue(new Callback<RegisterTransportBean>() {
                @Override
                public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                    activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    RegisterTransportBean registerTransportBean = response.body();
                    Log.i(TAG, "onResponse: " + registerTransportBean.getMessage());
                    if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.MODIFIED_SUCCESS)) {
                        showMessage(ApplicationConstants.REGISTER_SUCCESS);
                        User user = new User();
                        user.setFirstName(activityRegisterBinding.editTextFirstName.getText().toString());
                        user.setLastName(activityRegisterBinding.editTextLastName.getText().toString());
                        user.setMobileNumber(activityRegisterBinding.editTextMobileNo.getText().toString());
                        user.setEmailAddress(activityRegisterBinding.editTextEmailAddress.getText().toString());
                        user.setPassword(activityRegisterBinding.editTextPassword.getText().toString());
                        saveUser(user);
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
    }

    private void saveUser(User user) {
        userViewModel.insertUser(user);

    }

    private boolean validateFields() {
        boolean isValid = true;
        if (activityRegisterBinding.editTextFirstName.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutFirstName.setError(ApplicationConstants.FIRST_NAME_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutFirstName.setError(null);
        }

        if (activityRegisterBinding.editTextLastName.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutLastName.setError(ApplicationConstants.LAST_NAME_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutLastName.setError(null);
        }

        if (activityRegisterBinding.editTextEmailAddress.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutEmailAdds.setError(null);
        }

        if (activityRegisterBinding.editTextMobileNo.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutMobileNo.setError(ApplicationConstants.TELEPHONE_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutMobileNo.setError(null);
        }

        if (activityRegisterBinding.editTextPassword.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutPassword.setError(null);
        }

        if (activityRegisterBinding.editTextConfirmPassword.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutConfirmPwd.setError(ApplicationConstants.CONFIRM_PASSWORD_ERROR_MESSAGE);
        } else {
            if (!activityRegisterBinding.editTextPassword.getText().toString().isEmpty()) {
                if (!activityRegisterBinding.editTextConfirmPassword.getText().toString().equals(activityRegisterBinding.editTextPassword.getText().toString())) {
                    isValid = false;
                    activityRegisterBinding.textInputLayoutConfirmPwd.setError("Password Do Not Match");
                } else {
                    activityRegisterBinding.textInputLayoutConfirmPwd.setError(null);
                }
            }
        }


        return isValid;
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

package com.example.essentials.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.essentials.R;
import com.example.essentials.databinding.ActivityRegisterBinding;
import com.example.essentials.domain.User;
import com.example.essentials.fragment.CustomerDetailsFragment;
import com.example.essentials.service.RegisterCustomerService;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Main Activity";
    ActivityRegisterBinding activityRegisterBinding;
    UserViewModel userViewModel;
    boolean editUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!NetworkUtils.isNetworkConnected(this)) {
            EssentialsUtils.showAlertDialog(RegisterActivity.this, ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);

        } else {
            activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
            activityRegisterBinding.registerButton.setOnClickListener(this);
            initLayout();
            ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
            userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        }

        if (getIntent() != null && getIntent().getBooleanExtra(ApplicationConstants.EDIT_USER, false)) {
            editUser = true;
            hideFields();
            initFields();
            setTitle(getString(R.string.edit_user));
        } else {
            setTitle(getString(R.string.register_title));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: network connection and rotation


    }

    private void initFields() {
        User user = userViewModel.getUser(APIUtils.getLoggedInUserId(RegisterActivity.this));
        activityRegisterBinding.editTextFirstName.setText(user.getFirstName());
        activityRegisterBinding.editTextLastName.setText(user.getLastName());
        activityRegisterBinding.editTextEmailAddress.setText(user.getEmailAddress());
        activityRegisterBinding.editTextMobileNo.setText(user.getMobileNumber());
    }

    private void hideFields() {
        activityRegisterBinding.textInputLayoutConfirmPwd.setVisibility(View.GONE);
        activityRegisterBinding.editTextConfirmPassword.setVisibility(View.GONE);
        activityRegisterBinding.textInputLayoutPassword.setVisibility(View.GONE);
        activityRegisterBinding.editTextPassword.setVisibility(View.GONE);
        activityRegisterBinding.registerButton.setText(R.string.save);
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
                else if (!EssentialsUtils.isValidEmail(s.toString())) {
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


    private void registerCustomer(View view) {
        if (validateFields()) {
            RegisterCustomerService registerCustomerService = APIUtils.getRetrofit().create(RegisterCustomerService.class);
            Call<RegisterTransportBean> call = registerCustomerService.registerCustomer(activityRegisterBinding.editTextEmailAddress.getText().toString(), activityRegisterBinding.editTextFirstName.getText().toString(), activityRegisterBinding.editTextLastName.getText().toString(), activityRegisterBinding.editTextMobileNo.getText().toString(), activityRegisterBinding.editTextPassword.getText().toString());


            activityRegisterBinding.progressBar.setVisibility(View.VISIBLE);

            call.enqueue(new Callback<RegisterTransportBean>() {
                @Override
                public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                    activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    RegisterTransportBean registerTransportBean = response.body();
                    Log.i(TAG, "onResponse: " + registerTransportBean.getMessage());
                    if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.REGISTER_SUCCESS)) {
                        User user = new User();
                        user.setFirstName(activityRegisterBinding.editTextFirstName.getText().toString());
                        user.setLastName(activityRegisterBinding.editTextLastName.getText().toString());
                        user.setMobileNumber(activityRegisterBinding.editTextMobileNo.getText().toString());
                        user.setEmailAddress(activityRegisterBinding.editTextEmailAddress.getText().toString());
                        user.setPassword(activityRegisterBinding.editTextPassword.getText().toString());
                        user.setId(registerTransportBean.getCustomerId());
                        saveUser(user);

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra(ApplicationConstants.DISPLAY_TOAST, ApplicationConstants.REGISTER_SUCCESS);
                        startActivity(intent);

                    } else {
                        EssentialsUtils.showMessage(activityRegisterBinding.coordinatorLayout, registerTransportBean.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<RegisterTransportBean> call, Throwable throwable) {
                    activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    EssentialsUtils.showMessage(activityRegisterBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
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

        if (!editUser && activityRegisterBinding.editTextPassword.getText().toString().isEmpty()) {
            isValid = false;
            activityRegisterBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
        } else {
            activityRegisterBinding.textInputLayoutPassword.setError(null);
        }

        if (!editUser && activityRegisterBinding.editTextConfirmPassword.getText().toString().isEmpty()) {
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
        if (!editUser) {
            registerCustomer(view);
        } else {
            editUserDetails();
        }
    }

    private void editUserDetails() {
        if (validateFields()) {
            RegisterCustomerService registerCustomerService = APIUtils.getRetrofit().create(RegisterCustomerService.class);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", activityRegisterBinding.editTextEmailAddress.getText().toString())
                    .addFormDataPart("firstname", activityRegisterBinding.editTextFirstName.getText().toString())
                    .addFormDataPart("lastname", activityRegisterBinding.editTextLastName.getText().toString())
                    .addFormDataPart("telephone", activityRegisterBinding.editTextMobileNo.getText().toString())
                    .addFormDataPart("customerId", String.valueOf(APIUtils.getLoggedInUserId(RegisterActivity.this)))
                    .build();
            Call<RegisterTransportBean> call = registerCustomerService.editCustomer(APIUtils.getLoggedInToken(RegisterActivity.this), requestBody);


            call.enqueue(new Callback<RegisterTransportBean>() {
                @Override
                public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                    RegisterTransportBean registerTransportBean = response.body();
                    Log.i(TAG, "onResponse: " + registerTransportBean.getMessage());
                    if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.SUCCESSFULLY)) {
                        User user = userViewModel.getUser(APIUtils.getLoggedInUserId(RegisterActivity.this));
                        user.setFirstName(activityRegisterBinding.editTextFirstName.getText().toString());
                        user.setLastName(activityRegisterBinding.editTextLastName.getText().toString());
                        user.setMobileNumber(activityRegisterBinding.editTextMobileNo.getText().toString());
                        user.setEmailAddress(activityRegisterBinding.editTextEmailAddress.getText().toString());
                        userViewModel.updateUser(user);
                        showAlertDialog(RegisterActivity.this, ApplicationConstants.EDIT_CUSTOMER_TITLE, ApplicationConstants.EDIT_CUSTOMER_SUCCESS_MESSAGE);
                    }
                }

                @Override
                public void onFailure(Call<RegisterTransportBean> call, Throwable throwable) {
                    activityRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    EssentialsUtils.showMessage(activityRegisterBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
                    Log.e(this.getClass().getName(), throwable.toString());
                }
            });
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            finish();
            }
        });
        builder.setMessage(message);
        builder.create().show();
    }

}

package com.example.essentials.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.essentials.R;
import com.example.essentials.annotation.AnnotatedDeserializer;
import com.example.essentials.databinding.FragmentRegisterBinding;
import com.example.essentials.domain.User;
import com.example.essentials.service.RegisterCustomerService;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.utils.RetrofitUtils;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.essentials.utils.ApplicationConstants.PASSWORD_NOT_MATCH_ERROR_MESSAGE;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    FragmentRegisterBinding fragmentRegisterBinding;
    UserViewModel userViewModel;
    boolean editUser = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);

        } else {
            fragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
            fragmentRegisterBinding.registerButton.setOnClickListener(this);
            initLayout();
            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);


            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

            TextView titleView = actionBarView.findViewById(R.id.actionbar_view);

            if (getArguments() != null) {
                if (RegisterFragmentArgs.fromBundle(getArguments()).getEditUser()) {
                    editUser = true;
                    hideFields();
                    initFields();
                    titleView.setText(getResources().getString(R.string.edit_user));
                } else {
                    titleView.setText(getResources().getString(R.string.register_title));
                }
            }


            if (actionBar != null) {
                // enable the customized view and disable title
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setCustomView(actionBarView);
                //  actionBar.setTitle(getResources().getString(R.string.categories));
                actionBar.setDisplayShowTitleEnabled(false);


                // remove Burger Icon
                toolbar.setNavigationIcon(null);
            }
            actionBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                    DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            getActivity(), drawer, toolbar, R.string.drawer_open,
                            R.string.drawer_close);
                    // All that to re-synchronize the Drawer State
                    toggle.syncState();
                    getActivity().onBackPressed();
                }
            });
        }


        //TODO: network connection and rotation

        return fragmentRegisterBinding.getRoot();
    }

    private void initFields() {
        User user = userViewModel.getUser(APIUtils.getLoggedInUserId(getActivity()));
        fragmentRegisterBinding.editTextFirstName.setText(user.getFirstName());
        fragmentRegisterBinding.editTextLastName.setText(user.getLastName());
        fragmentRegisterBinding.editTextEmailAddress.setText(user.getEmailAddress());
        fragmentRegisterBinding.editTextMobileNo.setText(user.getMobileNumber());
    }

    private void hideFields() {
        fragmentRegisterBinding.textInputLayoutConfirmPwd.setVisibility(View.GONE);
        fragmentRegisterBinding.editTextConfirmPassword.setVisibility(View.GONE);
        fragmentRegisterBinding.textInputLayoutPassword.setVisibility(View.GONE);
        fragmentRegisterBinding.editTextPassword.setVisibility(View.GONE);
        fragmentRegisterBinding.registerButton.setText(R.string.save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLayout() {
        fragmentRegisterBinding.editTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutFirstName.setError(ApplicationConstants.FIRST_NAME_ERROR_MESSAGE);
                else
                    fragmentRegisterBinding.textInputLayoutFirstName.setError(null);

            }
        });

        fragmentRegisterBinding.editTextLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutLastName.setError(ApplicationConstants.LAST_NAME_ERROR_MESSAGE);
                else
                    fragmentRegisterBinding.textInputLayoutLastName.setError(null);

            }
        });


        fragmentRegisterBinding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
                else if (!EssentialsUtils.isValidEmail(s.toString())) {
                    fragmentRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE);
                } else
                    fragmentRegisterBinding.textInputLayoutEmailAdds.setError(null);

            }
        });

        fragmentRegisterBinding.editTextMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutMobileNo.setError(ApplicationConstants.TELEPHONE_ERROR_MESSAGE);
                else
                    fragmentRegisterBinding.textInputLayoutMobileNo.setError(null);

            }
        });

        fragmentRegisterBinding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
                else
                    fragmentRegisterBinding.textInputLayoutPassword.setError(null);

            }
        });

        fragmentRegisterBinding.editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentRegisterBinding.textInputLayoutConfirmPwd.setError(ApplicationConstants.CONFIRM_PASSWORD_ERROR_MESSAGE);
                else
                    fragmentRegisterBinding.textInputLayoutConfirmPwd.setError(null);

            }
        });

    }


    private void registerCustomer(View view) {
        if (validateFields()) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(RegisterTransportBean.class, new AnnotatedDeserializer<RegisterTransportBean>())
                    .setLenient().create();
            RegisterCustomerService registerCustomerService = RetrofitUtils.getRetrofit(gson).create(RegisterCustomerService.class);
            Call<RegisterTransportBean> call = registerCustomerService.registerCustomer(fragmentRegisterBinding.editTextEmailAddress.getText().toString(), fragmentRegisterBinding.editTextFirstName.getText().toString(), fragmentRegisterBinding.editTextLastName.getText().toString(), fragmentRegisterBinding.editTextMobileNo.getText().toString(), fragmentRegisterBinding.editTextPassword.getText().toString());


            fragmentRegisterBinding.progressBar.setVisibility(View.VISIBLE);

            call.enqueue(new Callback<RegisterTransportBean>() {
                @Override
                public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                    fragmentRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    RegisterTransportBean registerTransportBean = response.body();
                    if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.REGISTER_SUCCESS)) {
                        User user = new User();
                        user.setFirstName(fragmentRegisterBinding.editTextFirstName.getText().toString());
                        user.setLastName(fragmentRegisterBinding.editTextLastName.getText().toString());
                        user.setMobileNumber(fragmentRegisterBinding.editTextMobileNo.getText().toString());
                        user.setEmailAddress(fragmentRegisterBinding.editTextEmailAddress.getText().toString());
                        user.setPassword(fragmentRegisterBinding.editTextPassword.getText().toString());
                        user.setId(registerTransportBean.getCustomerId());

                        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.FIRST_NAME,user.getFirstName());
                        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.LAST_NAME,user.getFirstName());
                        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.MOBILE_NUMBER,user.getMobileNumber());
                        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.EMAIL,user.getEmailAddress());
                        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.PASSWORD,user.getPassword());
                        saveUser(user);

                        RegisterFragmentDirections.ActionNavTopRegisterToNavTopLogin action = RegisterFragmentDirections.actionNavTopRegisterToNavTopLogin();
                        action.setDisplayToast(true);
                        // Navigation.findNavController(rootView).navigate(action);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                    } else {
                        //EssentialsUtils.showMessage(fragmentRegisterBinding.coordinatorLayout, registerTransportBean.getMessage());
                        EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.DATA_ERROR, registerTransportBean.getMessage());
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, ApplicationConstants.EMAIL);
                    bundle.putLong(FirebaseAnalytics.Param.SUCCESS, 1);
                    APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                }

                @Override
                public void onFailure(Call<RegisterTransportBean> call, Throwable throwable) {
                    fragmentRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    // EssentialsUtils.showMessage(fragmentRegisterBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
                    APIUtils.getFirebaseCrashlytics().log(RegisterFragment.class.getName().concat( " ").concat(throwable.getMessage()));
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.SERVER_ERROR, ApplicationConstants.SOCKET_ERROR);
                }
            });
        }
    }

    private void saveUser(User user) {
        userViewModel.insertUser(user);

    }

    private boolean validateFields() {
        boolean isValid = true;
        if (fragmentRegisterBinding.editTextFirstName.getText().toString().isEmpty()) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutFirstName.setError(ApplicationConstants.FIRST_NAME_ERROR_MESSAGE);
        } else {
            fragmentRegisterBinding.textInputLayoutFirstName.setError(null);
        }

        if (fragmentRegisterBinding.editTextLastName.getText().toString().isEmpty()) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutLastName.setError(ApplicationConstants.LAST_NAME_ERROR_MESSAGE);
        } else {
            fragmentRegisterBinding.textInputLayoutLastName.setError(null);
        }

        if (fragmentRegisterBinding.editTextEmailAddress.getText().toString().isEmpty() || !EssentialsUtils.isValidEmail(fragmentRegisterBinding.editTextEmailAddress.getText().toString())) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
        } else {
            fragmentRegisterBinding.textInputLayoutEmailAdds.setError(null);
        }

        if (fragmentRegisterBinding.editTextMobileNo.getText().toString().isEmpty()) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutMobileNo.setError(ApplicationConstants.TELEPHONE_ERROR_MESSAGE);
        } else {
            fragmentRegisterBinding.textInputLayoutMobileNo.setError(null);
        }

        if (!editUser && fragmentRegisterBinding.editTextPassword.getText().toString().isEmpty()) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
        } else {
            fragmentRegisterBinding.textInputLayoutPassword.setError(null);
        }

        if (!editUser && fragmentRegisterBinding.editTextConfirmPassword.getText().toString().isEmpty()) {
            isValid = false;
            fragmentRegisterBinding.textInputLayoutConfirmPwd.setError(ApplicationConstants.CONFIRM_PASSWORD_ERROR_MESSAGE);
        } else {
            if (!fragmentRegisterBinding.editTextPassword.getText().toString().isEmpty()) {
                if (!fragmentRegisterBinding.editTextConfirmPassword.getText().toString().equals(fragmentRegisterBinding.editTextPassword.getText().toString())) {
                    isValid = false;
                    fragmentRegisterBinding.textInputLayoutConfirmPwd.setError(PASSWORD_NOT_MATCH_ERROR_MESSAGE);
                } else {
                    fragmentRegisterBinding.textInputLayoutConfirmPwd.setError(null);
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
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(RegisterTransportBean.class, new AnnotatedDeserializer<RegisterTransportBean>())
                    .setLenient().create();
            RegisterCustomerService registerCustomerService = RetrofitUtils.getRetrofit(gson).create(RegisterCustomerService.class);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(ApplicationConstants.EMAIL, fragmentRegisterBinding.editTextEmailAddress.getText().toString())
                    .addFormDataPart(ApplicationConstants.FIRST_NAME, fragmentRegisterBinding.editTextFirstName.getText().toString())
                    .addFormDataPart(ApplicationConstants.LAST_NAME, fragmentRegisterBinding.editTextLastName.getText().toString())
                    .addFormDataPart(ApplicationConstants.TELEPHONE, fragmentRegisterBinding.editTextMobileNo.getText().toString())
                    .addFormDataPart(ApplicationConstants.CUSTOMER_ID, String.valueOf(APIUtils.getLoggedInUserId(getActivity())))
                    .build();
            Call<RegisterTransportBean> call = registerCustomerService.editCustomer(APIUtils.getLoggedInToken(getActivity()), requestBody);


            call.enqueue(new Callback<RegisterTransportBean>() {
                @Override
                public void onResponse(Call<RegisterTransportBean> call, Response<RegisterTransportBean> response) {
                    RegisterTransportBean registerTransportBean = response.body();
                    if (registerTransportBean.getMessage() != null && registerTransportBean.getMessage().contains(ApplicationConstants.SUCCESSFULLY)) {
                        User user = userViewModel.getUser(APIUtils.getLoggedInUserId(getActivity()));
                        user.setFirstName(fragmentRegisterBinding.editTextFirstName.getText().toString());
                        user.setLastName(fragmentRegisterBinding.editTextLastName.getText().toString());
                        user.setMobileNumber(fragmentRegisterBinding.editTextMobileNo.getText().toString());
                        user.setEmailAddress(fragmentRegisterBinding.editTextEmailAddress.getText().toString());
                        userViewModel.updateUser(user);
                        showAlertDialog(getActivity(), ApplicationConstants.EDIT_CUSTOMER_TITLE, ApplicationConstants.EDIT_CUSTOMER_SUCCESS_MESSAGE);
                    }
                }

                @Override
                public void onFailure(Call<RegisterTransportBean> call, Throwable throwable) {
                    //    fragmentRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
                    //  EssentialsUtils.showMessage(fragmentRegisterBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.DATA_ERROR, ApplicationConstants.SOCKET_ERROR);
                    APIUtils.getFirebaseCrashlytics().log(RegisterFragment.class.getName().concat( " ").concat(throwable.getMessage()));
                    Log.e(this.getClass().getName(), throwable.toString());
                }
            });
        }
    }


    public void showAlertDialog(Context context, String title, String message) {
        androidx.appcompat.app.AlertDialog alertDialog;
        if (context instanceof Activity) {
            Activity activity = ((Activity) context);
            AlertDialog alert = new AlertDialog.Builder(context).create();
            if (!activity.isFinishing()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme);
                builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(RegisterFragmentDirections.actionNavTopRegisterToNavTopCustomerDetails());
                            }
                        });
                alertDialog = builder.create();
                alertDialog.show();
            }

        }
    }

}

package com.example.essentials.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.example.essentials.databinding.FragmentLoginBinding;
import com.example.essentials.domain.User;
import com.example.essentials.fragment.ProductFragmentArgs;
import com.example.essentials.fragment.WishlistFragmentDirections;
import com.example.essentials.service.LoginCustomerService;
import com.example.essentials.transport.LoginTransportBean;
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
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {
    private static final String TAG = "Login Activity";
    private static Retrofit retrofit = null;
    FragmentLoginBinding fragmentLoginBinding;
    UserViewModel userViewModel;
    SharedPreferences sharedpreferences;
    public static final String mypreference = ApplicationConstants.SHARED_PREF_NAME;
    public static final String userId = "userId";
    public static final String apiToken = "apiToken";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);

        } else {
            fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
            sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

            TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
            titleView.setText(getResources().getString(R.string.login_title));

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

            initLayout();

            if (getArguments() != null) {
                if (LoginFragmentArgs.fromBundle(getArguments()).getDisplayToast()) {
                    EssentialsUtils.showMessage(fragmentLoginBinding.coordinatorLayout, ApplicationConstants.REGISTER_SUCCESS);
                }
            }
            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        }
        return fragmentLoginBinding.getRoot();
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
        fragmentLoginBinding.registerHereTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionNavTopLoginToNavTopRegister());

            }
        });

        fragmentLoginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCustomer();
            }
        });

        fragmentLoginBinding.editTextEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
                else if (!EssentialsUtils.isValidEmail(s.toString())) {
                    fragmentLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_FORMAT_ERROR_MESSAGE);
                } else
                    fragmentLoginBinding.textInputLayoutEmailAdds.setError(null);

            }
        });

        fragmentLoginBinding.editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    fragmentLoginBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
                else
                    fragmentLoginBinding.textInputLayoutPassword.setError(null);

            }
        });

    }


    private void loginCustomer() {
        if (validateFields()) {
            LoginCustomerService loginCustomerService = APIUtils.getRetrofit().create(LoginCustomerService.class);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", ApplicationConstants.API_USER)
                    .addFormDataPart("key", ApplicationConstants.API_KEY)
                    .addFormDataPart("loginUser", fragmentLoginBinding.editTextEmailAddress.getText().toString())
                    .addFormDataPart("password", fragmentLoginBinding.editTextPassword.getText().toString())
                    .build();
            Call<LoginTransportBean> call = loginCustomerService.loginCustomer(requestBody);
            fragmentLoginBinding.progressBar.setVisibility(View.VISIBLE);

            call.enqueue(new Callback<LoginTransportBean>() {
                @Override
                public void onResponse(Call<LoginTransportBean> call, Response<LoginTransportBean> response) {
                    LoginTransportBean loginTransportBean = response.body();
                    fragmentLoginBinding.progressBar.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "onResponse: " + loginTransportBean.getMessage());
                    if (loginTransportBean.getMessage() != null && loginTransportBean.getMessage().contains(ApplicationConstants.LOGIN_SUCCESS)) {
                        EssentialsUtils.showMessage(fragmentLoginBinding.coordinatorLayout, ApplicationConstants.LOGIN_SUCCESS);
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
                            user.setPassword(fragmentLoginBinding.editTextPassword.getText().toString());
                            userViewModel.insertUser(user);
                        }
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt(ApplicationConstants.USER_ID, user.getId());
                        editor.putString(ApplicationConstants.API_TOKEN, user.getApiToken());
                        editor.putString(ApplicationConstants.USERNAME, (String) TextUtils.concat(user.getFirstName(), " ", user.getLastName()));
                        editor.commit();
                        Intent intent = new Intent(getActivity().getApplicationContext(), ProductActivity.class);
                        startActivity(intent);

                    } else {
                        EssentialsUtils.hideKeyboard(getActivity().getApplicationContext());
                        EssentialsUtils.showMessage(fragmentLoginBinding.coordinatorLayout, loginTransportBean.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<LoginTransportBean> call, Throwable throwable) {
                    fragmentLoginBinding.progressBar.setVisibility(View.INVISIBLE);
                    EssentialsUtils.showMessage(fragmentLoginBinding.coordinatorLayout, ApplicationConstants.SOCKET_ERROR);
//                    Log.e(this.getClass().getName(), throwable.toString());
                }
            });
        }
    }


    private boolean validateFields() {
        boolean isValid = true;

        if (fragmentLoginBinding.editTextEmailAddress.getText().toString().isEmpty()) {
            isValid = false;
            fragmentLoginBinding.textInputLayoutEmailAdds.setError(ApplicationConstants.EMAIL_ADDRESS_ERROR_MESSAGE);
        } else {
            fragmentLoginBinding.textInputLayoutEmailAdds.setError(null);
        }

        if (fragmentLoginBinding.editTextPassword.getText().toString().isEmpty()) {
            isValid = false;
            fragmentLoginBinding.textInputLayoutPassword.setError(ApplicationConstants.PASSWORD_ERROR_MESSAGE);
        } else {
            fragmentLoginBinding.textInputLayoutPassword.setError(null);
        }
        return isValid;
    }

}

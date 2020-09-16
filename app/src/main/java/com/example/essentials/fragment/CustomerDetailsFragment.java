package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.essentials.R;
import com.example.essentials.activity.DeliveryAddressActivity;
import com.example.essentials.domain.User;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

public class CustomerDetailsFragment extends Fragment {
    View rootView;
    UserViewModel userViewModel;
    Button editButton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_customer_details, container, false);
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
        } else {

            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

            TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
            titleView.setText(getResources().getString(R.string.customerdetails));

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
            initEditButton();
            getUserDetails();
            initCardView();
        }
        return rootView;
    }

    private void initCardView() {
        CardView cardView = (CardView) rootView.findViewById(R.id.deliveryAddsCardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DeliveryAddressActivity.class);
                intent.putExtra(ApplicationConstants.CUSTOMER_DETAILS,ApplicationConstants.YES);
                startActivity(intent);
            }
        });

    }

    private void initEditButton() {
        editButton = (Button) rootView.findViewById(R.id.edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),RegisterActivity.class);
//                intent.putExtra(ApplicationConstants.EDIT_USER,true);
//                startActivity(intent);

                CustomerDetailsFragmentDirections.ActionNavTopCustomerDetailsToNavTopRegister action = CustomerDetailsFragmentDirections.actionNavTopCustomerDetailsToNavTopRegister();
                action.setEditUser(true);
                // Navigation.findNavController(rootView).navigate(action);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });
    }

    private void getUserDetails() {
        User user = userViewModel.getUser(APIUtils.getLoggedInUserId(getActivity().getApplicationContext()));
        TextView usernameTxtView = rootView.findViewById(R.id.firstname_value_txtVw);
        usernameTxtView.setText(TextUtils.concat(user.getFirstName()));

        TextView lastNameTxtView = rootView.findViewById(R.id.lastname_value_txtVw);
        lastNameTxtView.setText(TextUtils.concat(user.getLastName()));

        TextView emailTxtView = rootView.findViewById(R.id.emailadds_value_txtVw);
        emailTxtView.setText(user.getEmailAddress());

        TextView mobileTxtView = rootView.findViewById(R.id.mobilenumber_value_txtVw);
        mobileTxtView.setText(user.getMobileNumber());

    }
}

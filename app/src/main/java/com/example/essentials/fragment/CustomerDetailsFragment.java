package com.example.essentials.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.essentials.R;
import com.example.essentials.domain.User;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

public class CustomerDetailsFragment extends Fragment {
    View rootView;
    UserViewModel userViewModel;
    Button editButton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_customer_details, container, false);
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        initEditButton();
        getUserDetails();
        return rootView;
    }

    private void initEditButton() {
        editButton = (Button)  rootView.findViewById(R.id.edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),RegisterActivity.class);
//                intent.putExtra(ApplicationConstants.EDIT_USER,true);
//                startActivity(intent);
            }
        });
    }

    private void getUserDetails() {
        User user =userViewModel.getUser(APIUtils.getLoggedInUserId(getActivity().getApplicationContext()));
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

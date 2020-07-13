package com.example.essentials.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.essentials.R;

public class NavTopDrawerFragment extends Fragment {

    public View onCreateView (LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_top_drawer_nav, container, false);
    }
}

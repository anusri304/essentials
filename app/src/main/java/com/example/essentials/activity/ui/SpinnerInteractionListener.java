package com.example.essentials.activity.ui;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    boolean userSelect = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (userSelect) {
            // Your selection handling code here
            userSelect = false;
            Log.d("Anu","Anu");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
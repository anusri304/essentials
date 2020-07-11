package com.example.essentials.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.essentials.R;
import com.example.essentials.activity.RegisterActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class EssentialsUtils {

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void showMessage(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showAlertDialog(Context context){
        AlertDialog.Builder builder = new  AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(ApplicationConstants.NO_INTERNET_TITLE);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Activity) context).finish();
            }
        });
        builder.setMessage(ApplicationConstants.NO_INTERNET_MESSAGE);
        builder.create().show();
    }

}

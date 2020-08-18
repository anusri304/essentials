package com.example.essentials.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.domain.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class EssentialsUtils {

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void showMessage(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showNetworkAlertDialog(Context context){
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

    public static void showMessageAlertDialog(Context context){
        if ( context instanceof Activity ) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setTitle(ApplicationConstants.DATA_ERROR);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Activity) context).finish();
                    }
                });
                builder.setMessage(ApplicationConstants.ERROR_RETRIEVE_MESSAGE);
                builder.create().show();
            }
        }
    }

    public static void showMessageAlertDialog1(Context context,String title, String message ){
        if (  context instanceof Activity) {
            Activity activity = ((Activity) context);
            if (!activity.isFinishing()) {
//                        new MaterialAlertDialogBuilder(getActivity(), R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
//                                .setTitle("Test")
//                                .setMessage("Test1")
//                                .setPositiveButton("Ok", null)
//                                .show();
                new MaterialAlertDialogBuilder(context,R.style.RoundShapeTheme).setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
    }

    public static int getSpan(Context context) {
        float scaledWidth = getScaledWidth(context);
        if (scaledWidth > 600 && scaledWidth <= 900) {
            return 2;
        } else if (scaledWidth > 900) {
            return 3;
        } else return 1;
    }

    public static float getScaledWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.widthPixels / displayMetrics.scaledDensity);
    }

    /**
     * Call this method so that it hides the keyboard
     *
     * @param context context
     */
    public static void hideKeyboard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static List<ProductPresentationBean> getProductPresentationBeans(List<Product> products) {
        List<ProductPresentationBean> productPresentationBeans = new ArrayList<ProductPresentationBean>();
        for (Product product : products) {
            ProductPresentationBean productPresentationBean = new ProductPresentationBean();
            productPresentationBean.setId(product.getId());
            productPresentationBean.setName(product.getName());
            productPresentationBean.setDescription(product.getDescription());
            productPresentationBean.setInStock(product.getInStock());
            productPresentationBean.setDiscPerc(product.getDiscPerc());
            productPresentationBean.setSpecial(product.getSpecial());
            productPresentationBean.setPrice(product.getPrice());
            productPresentationBean.setImage(product.getImagePath());
            productPresentationBeans.add(productPresentationBean);
        }
        return productPresentationBeans;
    }

}

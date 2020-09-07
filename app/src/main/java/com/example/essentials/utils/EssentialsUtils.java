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
import com.example.essentials.activity.bean.AddressPresentationBean;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.CategoryPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Category;
import com.example.essentials.domain.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EssentialsUtils {

    static androidx.appcompat.app.AlertDialog alertDialog;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void showMessage(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showAlertDialog(Context context, String title, String message ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Activity) context).finish();
            }
        });
        builder.setMessage(message);
        builder.create().show();
    }


    public static void showMessageAlertDialog(Context context, String title, String message) {
        if (context instanceof Activity) {
            Activity activity = ((Activity) context);
            AlertDialog alert = new AlertDialog.Builder(context).create();
            if (!activity.isFinishing()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme);
                builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ok", null);

                boolean isShowing = isAlertDialogShowing(alertDialog);

                if (!isShowing) {
                    alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        }
    }

    public static boolean isAlertDialogShowing(androidx.appcompat.app.AlertDialog thisAlertDialog) {
        if (thisAlertDialog != null) {
            return thisAlertDialog.isShowing();
        } else {
            return false;
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
            productPresentationBean.setCategoryId(product.getCategoryId());
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

    public static List<CategoryPresentationBean> getCategoryPresentationBean(List<Category> categories) {
        List<CategoryPresentationBean> categoryPresentationBeans = new ArrayList<CategoryPresentationBean>();
        for (Category category : categories) {
            CategoryPresentationBean categoryPresentationBean = new CategoryPresentationBean();
            categoryPresentationBean.setId(category.getId());
            categoryPresentationBean.setName(category.getName());
            categoryPresentationBeans.add(categoryPresentationBean);
        }
        return categoryPresentationBeans;

    }


    public static List<AddressPresentationBean> getAddressPresentationBeans(List<Address> addressList) {
        List<AddressPresentationBean> addressPresentationBeans = new ArrayList<AddressPresentationBean>();
        for (Address address : addressList) {
            AddressPresentationBean addressPresentationBean = new AddressPresentationBean();
            addressPresentationBean.setId(address.getId());
            addressPresentationBean.setName(address.getFirstName() + "\n" + address.getLastName() + "\n"+ address.getAddressLine1() + "\n"+address.getAddressLine2()+ "\n"+ address.getCity()+ "\n"+address.getPostalCode()+ "\n"+address.getCountry());
            addressPresentationBeans.add(addressPresentationBean);
        }
        return addressPresentationBeans;
    }

    public static List<CartPresentationBean> getCartPresentationBeans(List<Cart> cartItems, List<ProductPresentationBean> filteredProductPresentationBeans) {
        List<CartPresentationBean> cartPresentationBeans = new ArrayList<CartPresentationBean>();
        for (Cart cart : cartItems) {
            CartPresentationBean cartPresentationBean = new CartPresentationBean();
            cartPresentationBean.setId(cart.getId());
            cartPresentationBean.setQuantity(cart.getQuantity());

            ProductPresentationBean matchingProductPresentationBean = filteredProductPresentationBeans.stream().filter(productPresentationBean -> productPresentationBean.getId() == cart.getProductId()).findAny().get();
            cartPresentationBean.setName(matchingProductPresentationBean.getName());
            cartPresentationBean.setPrice(matchingProductPresentationBean.getPrice());
            cartPresentationBean.setProductId(matchingProductPresentationBean.getId());
            cartPresentationBean.setImage(matchingProductPresentationBean.getImage());
            cartPresentationBeans.add(cartPresentationBean);

        }
        return cartPresentationBeans;
    }


    public static double getTotal(List<CartPresentationBean> cartPresentationBeans){
        return cartPresentationBeans.stream().mapToDouble(cartPresentationBean -> Double.parseDouble(cartPresentationBean.getPrice().substring(1)) * cartPresentationBean.getQuantity()).sum();
    }

    public static String formatTotal(double total){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("##.##");
        df.setDecimalFormatSymbols(otherSymbols);
        return ApplicationConstants.CURRENCY_SYMBOL.concat(df.format(total));
    }
}

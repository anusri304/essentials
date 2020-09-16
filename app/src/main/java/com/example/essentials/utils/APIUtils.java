package com.example.essentials.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

public class APIUtils {
    private static FirebaseAnalytics firebaseAnalytics = null;
    private static FirebaseCrashlytics firebaseCrashlytics = null;

    public static boolean isUserLogged(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        return apiToken.equalsIgnoreCase("") ? false : true;
    }

    public static String getLoggedInUserName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getString(ApplicationConstants.USERNAME, "");
    }

    public static int getLoggedInUserId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getInt(ApplicationConstants.USER_ID, 0);
    }

    public static String getLoggedInToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        return pref.getString(ApplicationConstants.API_TOKEN, "");
    }


    public static void logViewItemsAnalyticsEvent(Context context, List<ProductPresentationBean> productPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (ProductPresentationBean productPresentationBean : productPresentationBeans) {
            sb.append(productPresentationBean.getId());
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
        bundle.putString(ApplicationConstants.ITEM_ID_LIST, sb.substring(0, sb.lastIndexOf(",")));
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    public static void logAddToCartAnalyticsEvent(Context context, ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public static void logCheckoutAnalyticsEvent(Context context, CartPresentationBean cartPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, cartPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public static void logRemoveFromCartAnalyticsEvent(Context context, CartPresentationBean cartPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, cartPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, bundle);
    }

    public static void logAddToWishlistAnalyticsEvent(Context context, ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
    }

    public static void logAddOrderAnalyticsEvent(Context context, int orderId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ApplicationConstants.ORDER_ID, orderId);
        APIUtils.getFirebaseAnalytics(context).logEvent(ApplicationConstants.ADD_ORDER, bundle);
    }


    public static void logViewCartAnalyticsEvent(Context context, ProductPresentationBean productPresentationBean) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, ApplicationConstants.CURRENCY_SYMBOL);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productPresentationBean.getName());
        bundle.putString(FirebaseAnalytics.Param.VALUE, productPresentationBean.getPrice());
        APIUtils.getFirebaseAnalytics(context).logEvent(FirebaseAnalytics.Event.VIEW_CART, bundle);
    }


    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        return firebaseAnalytics;
    }

    public static FirebaseCrashlytics getFirebaseCrashlytics() {
        if (firebaseCrashlytics == null) {
            firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        }
        return firebaseCrashlytics;
    }
}

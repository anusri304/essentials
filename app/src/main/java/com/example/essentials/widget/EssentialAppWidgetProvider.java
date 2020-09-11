package com.example.essentials.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.ProductActivity;
import com.example.essentials.service.DisplayPromotionProductService;

public class EssentialAppWidgetProvider extends AppWidgetProvider {

    static String TAG= "EssentialAppWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String productName, String imagePath, String price,  int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.essentials_app_widget);
        views.setTextViewText(R.id.appwidget_productName, productName);

        try {
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imagePath)
                    .submit(820, 300)
                    .get();

            views.setImageViewBitmap(R.id.productImageView, bitmap);
        } catch (Exception e) {
            Log.d(TAG,"Error loading image in EssentialAppWidgetProvider");
        }
        views.setTextViewText(R.id.appwidget_productPrice, price);

        Intent intent = new Intent(context, ProductActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_RelativeLayout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        DisplayPromotionProductService.startActionDisplayPromotionProductWidgets(context);
    }

    public static void updateProductWidget(Context context, AppWidgetManager appWidgetManager,  String productName, String imagePath, String price, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, productName, imagePath, price, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

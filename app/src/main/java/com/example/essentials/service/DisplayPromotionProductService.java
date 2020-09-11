package com.example.essentials.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.example.essentials.domain.Product;
import com.example.essentials.repository.ProductRepository;
import com.example.essentials.utils.MyApplication;
import com.example.essentials.widget.EssentialAppWidgetProvider;

public class DisplayPromotionProductService extends IntentService implements LifecycleObserver {
    public static final String ACTION_DISPLAY_PROMOTION_PRODUCT_WIDGETS = "com.example.android.essentials.action.display_promotion_product_widgets";
    ProductRepository productRepository;

    public DisplayPromotionProductService() {
        super("DisplayPromotionProductService");
        productRepository = new ProductRepository(MyApplication.getInstance());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DISPLAY_PROMOTION_PRODUCT_WIDGETS.equals(action)) {
                handleActionDisplayPromotionProduct();
            }
        }
    }

    private void handleActionDisplayPromotionProduct() {
        Product promotionProduct = productRepository.getPromotionProduct();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, EssentialAppWidgetProvider.class));
        EssentialAppWidgetProvider.updateProductWidget(this,appWidgetManager,promotionProduct.getName(),promotionProduct.getImagePath(), promotionProduct.getSpecial(),appWidgetIds);

    }

    public static void startActionDisplayPromotionProductWidgets(Context context) {
        Intent intent = new Intent(context, DisplayPromotionProductService.class);
        intent.setAction(ACTION_DISPLAY_PROMOTION_PRODUCT_WIDGETS);
        context.startService(intent);
    }
}

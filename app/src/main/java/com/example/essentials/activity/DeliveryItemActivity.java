package com.example.essentials.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.DeliveryRecyclerViewAdapter;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryItemActivity extends AppCompatActivity {
    CartViewModel cartViewModel;
    List<Cart> cartItems = new ArrayList<>();
    DeliveryRecyclerViewAdapter deliveryRecyclerViewAdapter;
    ProductViewModel productViewModel;
    List<Product> products = new ArrayList<>();
    TextView deliveryDateValueView;
    TextView totalValueTextView;
    Button confirmOrder;
    static androidx.appcompat.app.AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.delivery_items));
        setContentView(R.layout.activity_delivery);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        confirmOrder = findViewById(R.id.order_button);

        initOrder();
        getAllProducts();
    }

    private void initOrder() {
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderAlertDialog(DeliveryItemActivity.this);
            }
        });
    }


    public  void showOrderAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(ApplicationConstants.TITLE_ORDER);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  Intent intent = new Intent(DeliveryItemActivity.this,ProductActivity.class);
                  startActivity(intent);
            }
        });
        builder.setMessage(ApplicationConstants.ORDER_SUCESS_MESSAGE);
        builder.create().show();
    }

    private void getCartItems() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            if (!cartItems.isEmpty()) {
                setData(cartItems);
            }
        });
    }


    private void getAllProducts() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;
            Log.d("products", String.valueOf(products.size()));
            getCartItems();

        });
    }
    private void setData(List<Cart> cartItems) {
        List<ProductPresentationBean> filteredProductPresentationBeans = EssentialsUtils.getProductPresentationBeans(products).stream().filter(productPresentationBean ->
                cartItems.stream().map(cart -> cart.getProductId()).collect(Collectors.toSet())
                        .contains(productPresentationBean.getId())).collect(Collectors.toList());
        if (filteredProductPresentationBeans != null ) {

            setProductData(EssentialsUtils.getCartPresentationBeans(cartItems,filteredProductPresentationBeans));
        }

    }

    private void setProductData(List<CartPresentationBean> cartPresentationBeans) {
        deliveryRecyclerViewAdapter = new DeliveryRecyclerViewAdapter(DeliveryItemActivity.this, cartPresentationBeans, cartViewModel);
        RecyclerView recyclerView = findViewById(R.id.rv_delivery_items);
        recyclerView.setAdapter(deliveryRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(DeliveryItemActivity.this, EssentialsUtils.getSpan(DeliveryItemActivity.this));
        recyclerView.setLayoutManager(manager);

        deliveryDateValueView = (TextView) findViewById(R.id.deliveryDateValueView);

        totalValueTextView = (TextView) findViewById(R.id.totalValue);


      totalValueTextView.setText(EssentialsUtils.formatTotal(EssentialsUtils.getTotal(cartPresentationBeans)));
        LocalDate today = LocalDate.now().plusDays(2);

      //  LocalDate.parse(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(today)).plusDays(2);

        deliveryDateValueView.setText( DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(today));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

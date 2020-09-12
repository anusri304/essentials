package com.example.essentials.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.essentials.domain.Address;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.domain.OrderProduct;
import com.example.essentials.domain.Product;
import com.example.essentials.service.CartService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.AddressViewModel;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.OrderCustomerViewModel;
import com.example.essentials.viewmodel.OrderProductViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryItemActivity extends AppCompatActivity {
    CartViewModel cartViewModel;
    List<Cart> cartItems = new ArrayList<>();
    DeliveryRecyclerViewAdapter deliveryRecyclerViewAdapter;
    ProductViewModel productViewModel;
    OrderCustomerViewModel orderCustomerViewModel;
    OrderProductViewModel orderProductViewModel;
    List<Product> products = new ArrayList<>();
    TextView deliveryDateValueView;
    TextView totalValueTextView;
    Button confirmOrder;
    static androidx.appcompat.app.AlertDialog alertDialog;
    double total;
    List<CartPresentationBean> cartPresentationBeans;
    int addressId;
    TextView addsTxtView;
    AddressViewModel addressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.delivery_items));
        setContentView(R.layout.activity_delivery);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent() != null) {
            addressId = getIntent().getIntExtra(ApplicationConstants.ADDRESS_ID, 0);
        }

        ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        addressViewModel = new ViewModelProvider(this, factory).get(AddressViewModel.class);

        orderCustomerViewModel = new ViewModelProvider(this, factory).get(OrderCustomerViewModel.class);

        orderProductViewModel = new ViewModelProvider(this, factory).get(OrderProductViewModel.class);

        confirmOrder = findViewById(R.id.order_button);
        initAddress();

        initOrder();
        getAllProducts();
    }


    private void initAddress() {
        Address address=  addressViewModel.getAddressForId(addressId);
        addsTxtView = findViewById(R.id.deliveryAddsView);

        addsTxtView.setText(address.getFirstName() + "\n" + address.getLastName() + "\n"+ address.getAddressLine1() + "\n"+address.getAddressLine2()+ "\n"+ address.getCity()+ "\n"+address.getPostalCode()+ "\n"+address.getCountry());
    }

    private void initOrder() {
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrdersInDB();
            }
        });
    }

    private void saveOrdersInDB() {
        OrderCustomer orderCustomer = new OrderCustomer();
        orderCustomer.setAddressId(addressId);
        orderCustomer.setId(new Random().nextInt());
        orderCustomer.setUserId(APIUtils.getLoggedInUserId(DeliveryItemActivity.this));
        orderCustomer.setPaymentCustomerName(APIUtils.getLoggedInUserName(DeliveryItemActivity.this));
        orderCustomer.setTotal(total);
        orderCustomer.setStatus(ApplicationConstants.STATUS_PENDING);
        orderCustomer.setDateAdded(new Date());
        orderCustomerViewModel.insertOrderCustomer(orderCustomer);
        saveOrderProducts(orderCustomer.getId());
    }

    private void callCartEndPoint(CartPresentationBean cartPresentationBean) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        CartService cartService = APIUtils.getRetrofit().create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(cartPresentationBean.getProductId()))
                .addFormDataPart("customerId", String.valueOf(userId))
                .build();
        Call<CartTransportBean> call = cartService.removeFromCart(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                if (response.isSuccessful()) {
                    deleteCartItemsFromDB(userId, cartPresentationBean.getProductId());
                    APIUtils.logRemoveFromCartAnalyticsEvent(DeliveryItemActivity.this,cartPresentationBean);
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {

            }
        });
    }

    private void saveOrderProducts(int orderId) {
        cartPresentationBeans.stream().forEach(cartPresentationBean -> {
            saveOrderProductInDB(orderId, cartPresentationBean);
        });
    }

    private void saveOrderProductInDB(int orderId, CartPresentationBean cartPresentationBean) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderId(orderId);
        orderProduct.setPrice(Double.valueOf(cartPresentationBean.getPrice().substring(1)));
        orderProduct.setQuantity(cartPresentationBean.getQuantity());
        orderProduct.setTotal(total);
        orderProduct.setProductId(cartPresentationBean.getProductId());
        orderProduct.setProductName(cartPresentationBean.getName());
        orderProduct.setProductImage(cartPresentationBean.getImage());
        orderProductViewModel.insertOrderProduct(orderProduct);

        callCartEndPoint(cartPresentationBean);
    }

    private void deleteCartItemsFromDB(int userId, int productId) {
        Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, productId);
        if (cart != null) {
            cartViewModel.deleteCartItems(cart);
        }
        showOrderAlertDialog(DeliveryItemActivity.this);

    }


    public void showOrderAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(ApplicationConstants.TITLE_ORDER);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(DeliveryItemActivity.this, ProductActivity.class);
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
        if (filteredProductPresentationBeans != null) {

            setProductData(EssentialsUtils.getCartPresentationBeans(cartItems, filteredProductPresentationBeans));
        }

    }

    private void setProductData(List<CartPresentationBean> cartPresentationBeans) {
        this.cartPresentationBeans = cartPresentationBeans;
        deliveryRecyclerViewAdapter = new DeliveryRecyclerViewAdapter(DeliveryItemActivity.this, cartPresentationBeans);
        RecyclerView recyclerView = findViewById(R.id.rv_delivery_items);
        recyclerView.setAdapter(deliveryRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(DeliveryItemActivity.this, EssentialsUtils.getSpan(DeliveryItemActivity.this));
        recyclerView.setLayoutManager(manager);

        deliveryDateValueView = (TextView) findViewById(R.id.deliveryDateValueView);

        totalValueTextView = (TextView) findViewById(R.id.totalValue);

        total = EssentialsUtils.getTotal(cartPresentationBeans);
        totalValueTextView.setText(EssentialsUtils.formatTotal(EssentialsUtils.getTotal(cartPresentationBeans)));
        LocalDate today = LocalDate.now().plusDays(2);

        //  LocalDate.parse(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(today)).plusDays(2);

        deliveryDateValueView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(today));
        if(cartPresentationBeans !=null && cartPresentationBeans.size()>0) {
            logAnalyticsEvent(cartPresentationBeans);
        }
    }

    private void logAnalyticsEvent(List<CartPresentationBean> cartPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (CartPresentationBean cartPresentationBean : cartPresentationBeans) {
            sb.append(cartPresentationBean.getName());
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, ApplicationConstants.CART_PRESENTATION_BEAN);
        bundle.putString(ApplicationConstants.ITEMS, sb.substring(0, sb.lastIndexOf(",")));
        APIUtils.getFirebaseAnalytics(DeliveryItemActivity.this).logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
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

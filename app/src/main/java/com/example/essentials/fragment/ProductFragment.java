package com.example.essentials.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.LoginActivity;
import com.example.essentials.activity.RegisterActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.domain.User;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.RegisterCustomerService;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.example.essentials.transport.RegisterTransportBean;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductFragment extends Fragment implements ProductRecyclerViewAdapter.ListItemClickListener {
    private static Retrofit retrofit = null;
    String TAG = "ProductFragment";
    List<ProductPresentationBean> productPresentationBeans = new ArrayList<>();
    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        getProductsForCustomer();
        return rootView;
    }

    private void getProductsForCustomer() {
        ProductService productService = getRetrofit().create(ProductService.class);
        Call<ProductListTransportBean> call = productService.getAllProducts();

        call.enqueue(new Callback<ProductListTransportBean>() {
            @Override
            public void onResponse(Call<ProductListTransportBean> call, Response<ProductListTransportBean> response) {
                ProductListTransportBean productTransportBeans = response.body();
                Log.i(TAG, "onResponse: " + productTransportBeans.getProducts().size());
                List<ProductPresentationBean> productPresentationBeans = new ArrayList<ProductPresentationBean>();
                for(ProductTransportBean productTransportBean:productTransportBeans.getProducts()){
                    ProductPresentationBean productPresentationBean = new ProductPresentationBean();
                    productPresentationBean.setId(Integer.valueOf(productTransportBean.getProductId()));
                    //TODO: Replace the path in Opencart
                    productPresentationBean.setImage(productTransportBean.getImage().replace("http://localhost/OpenCart/",ApplicationConstants.BASE_URL));
                    productPresentationBeans.add(productPresentationBean);
                }
                setData(productPresentationBeans);
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }

    private void setData(List<ProductPresentationBean> productPresentationBeans) {
        ProductRecyclerViewAdapter adapter = new ProductRecyclerViewAdapter(getActivity(), productPresentationBeans, this);
        RecyclerView recyclerView =rootView.findViewById(R.id.rv_products);
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(manager);

    }

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
// The App will not crash for malformed JSON.
        Gson gson = new GsonBuilder().setLenient().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}

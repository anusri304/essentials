package com.example.essentials.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.ProductDetailActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.domain.Product;
import com.example.essentials.service.ProductService;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
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
    List<ProductPresentationBean> productPresentationBeans;
    View rootView;
    ProductRecyclerViewAdapter adapter;
    ProductViewModel productViewModel;
    List<Product> products = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        getProductsForCustomer();

        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        observeChanges();
        return rootView;
    }

    private void observeChanges() {
        Log.d("Anandhi...", "observeChanges");
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;

            if (!products.isEmpty()) {
                setData(EssentialsUtils.getProductPresentationBeans(products));
            } else {
                // No data is retrieved. Check if there is no internet
                if (!NetworkUtils.isNetworkConnected(getActivity().getApplicationContext())) {
                    EssentialsUtils.showNetworkAlertDialog(getActivity().getApplicationContext());
                } else { // If there is internet then there is an error retrieving data. display error retrieve message
                    EssentialsUtils.showMessageAlertDialog(getActivity().getApplicationContext());
                }
            }
        });
    }

    private void getProductsForCustomer() {
        ProductService productService = getRetrofit().create(ProductService.class);
        Call<ProductListTransportBean> call = productService.getAllProducts();

        call.enqueue(new Callback<ProductListTransportBean>() {
            @Override
            public void onResponse(Call<ProductListTransportBean> call, Response<ProductListTransportBean> response) {
                ProductListTransportBean productTransportBeans = response.body();
                Log.i(TAG, "onResponse: " + productTransportBeans.getProducts().size());
                productPresentationBeans = new ArrayList<ProductPresentationBean>();
                for (ProductTransportBean productTransportBean : productTransportBeans.getProducts()) {
                    ProductPresentationBean productPresentationBean = new ProductPresentationBean();
                    productPresentationBean.setId(Integer.valueOf(productTransportBean.getProductId()));
                    //TODO: Replace the path in Opencart
                    productPresentationBean.setImage(productTransportBean.getImage().replace("http://localhost/OpenCart/", ApplicationConstants.BASE_URL));
                    // productPresentationBean.setImage("http://10.0.75.1/Opencart/image/cache/catalog/demo/canon_eos_5d_1-228x228.jpg");
                    productPresentationBean.setName(productTransportBean.getName());
                    productPresentationBean.setPrice(productTransportBean.getPrice());
                    //TODO: Get full description
                    productPresentationBean.setDescription(productTransportBean.getDescription());
                    //TODO: Only get special products
                    productPresentationBean.setSpecial(productTransportBean.getSpecial().equals(ApplicationConstants.FALSE) ? "" : productTransportBean.getSpecial());
                    //TODO: get disc perc
                    productPresentationBean.setDiscPerc(productTransportBean.getSpecial().equals(ApplicationConstants.FALSE) ? "" : productTransportBean.getDiscPerc());
                    //TODO: get inStock
                    productPresentationBean.setInStock(productTransportBean.getInStock());
                    productPresentationBeans.add(productPresentationBean);
                }
                saveorUpdateProduct(productPresentationBeans);
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });

    }



    private void saveorUpdateProduct(List<ProductPresentationBean> productPresentationBeans) {
        for (ProductPresentationBean productPresentationBean : productPresentationBeans) {
            Product product = productViewModel.getProduct(productPresentationBean.getId());
            if (product != null) {
                product.setId(Integer.valueOf(productPresentationBean.getId()));
                Log.d("update", String.valueOf(productPresentationBean.getId()));
                product.setName(productPresentationBean.getName());
                //  product.setImagePath(imagePath);
                product.setPrice(productPresentationBean.getPrice());
                product.setDescription(productPresentationBean.getDescription());
                product.setSpecial(productPresentationBean.getSpecial());
                product.setDiscPerc(productPresentationBean.getDiscPerc());
                product.setInStock(productPresentationBean.getInStock());
                productViewModel.updateProduct(product, getActivity().getApplicationContext(), productPresentationBean.getImage());
            } else {
                product = new Product();
                Log.d("insert", String.valueOf(productPresentationBean.getId()));
                product.setId(Integer.valueOf(productPresentationBean.getId()));
                product.setName(productPresentationBean.getName());
                product.setPrice(productPresentationBean.getPrice());
                product.setDescription(productPresentationBean.getDescription());
                product.setSpecial(productPresentationBean.getSpecial());
                product.setDiscPerc(productPresentationBean.getDiscPerc());
                product.setInStock(productPresentationBean.getInStock());
                productViewModel.insertProduct(product, getActivity().getApplicationContext(), productPresentationBean.getImage());
            }
        }

    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
        ProductPresentationBean productPresentationBean = EssentialsUtils.getProductPresentationBeans(products).get(clickedItemIndex);
//        ProductFragmentDirections.NavigateToProductDetailFragment action = ProductFragmentDirections.navigateToProductDetailFragment(productPresentationBean);
//
//       Navigation.findNavController(rootView).navigate(action);
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(ApplicationConstants.PRODUCT_PRESENTATION_BEAN, productPresentationBean);
        startActivity(intent);

//https://www.youtube.com/watch?v=vx1-V3HH0IU
    }

    private void setData(List<ProductPresentationBean> productPresentationBeans) {
        adapter = new ProductRecyclerViewAdapter(getActivity(), productPresentationBeans, this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_products);
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
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

    public void filter(String query) {
        adapter.performFilter(query);
        Log.d("TEsting", query);

    }

}

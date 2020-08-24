package com.example.essentials.fragment;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.essentials.activity.ProductActivity;
import com.example.essentials.activity.ProductDetailActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.adapter.ProductRecyclerViewAdapter;
import com.example.essentials.domain.Cart;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.Wishlist;
import com.example.essentials.service.ProductService;
import com.example.essentials.service.WishlistService;
import com.example.essentials.transport.CustomerCartListTransportBean;
import com.example.essentials.transport.CustomerCartTransportBean;
import com.example.essentials.transport.CustomerWishListTransportBean;
import com.example.essentials.transport.CustomerWishTransportBean;
import com.example.essentials.transport.ProductListTransportBean;
import com.example.essentials.transport.ProductTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.CartViewModel;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    CartViewModel cartViewModel;
    WishlistViewModel wishlistViewModel;
    List<Product> products = new ArrayList<>();
    List<Cart> cartItems = new ArrayList<>();
    List<Wishlist> wishListItems = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        getAllProducts();
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
        observeChanges();
        observeCartChanges();
        return rootView;
    }

    private void observeCartChanges() {
        cartViewModel.getAllCartItems().observe(this, objCart -> {
            cartItems = objCart;
            int totalQuantity = cartItems.stream().mapToInt(cart -> cart.getQuantity()).sum();
            if(APIUtils.isUserLogged(getActivity())) {
                drawBadge(totalQuantity);
            }

        });
    }

    private void drawBadge(int number) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.navigationView);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_cart);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }


    private void observeChanges() {
        productViewModel.getAllProducts().observe(this, objProducts -> {
            products = objProducts;

            if (!products.isEmpty()) {
                setData(EssentialsUtils.getProductPresentationBeans(products));
            }
        });
    }

    private void getProductsForCustomer() {
        ProductService productService = APIUtils.getRetrofit().create(ProductService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerCartListTransportBean> call = productService.getProductsForCustomer(String.valueOf(userId), apiToken);
        //Save Products to cart
        call.enqueue(new Callback<CustomerCartListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerCartListTransportBean> call, Response<CustomerCartListTransportBean> response) {
                if (response.isSuccessful()) {
                    CustomerCartListTransportBean customerCartListTransportBeans = response.body();
                    if (customerCartListTransportBeans != null && customerCartListTransportBeans.getProducts()!=null && customerCartListTransportBeans.getProducts().size() > 0) {
                        for (CustomerCartTransportBean customercartTransportBean : customerCartListTransportBeans.getProducts()) {
                            Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, Integer.parseInt(customercartTransportBean.getProductId()));
                            if (cart == null) {
                                cart = new Cart();
                                cart.setProductId(Integer.valueOf(customercartTransportBean.getProductId()));
                                cart.setQuantity(Integer.valueOf(customercartTransportBean.getQuantity()));
                                cart.setUserId(userId);
                                cartViewModel.insertCartItems(cart);
                            }
                        }
                        getWishlistProductsForCustomer();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerCartListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });
    }

    private void getWishlistProductsForCustomer() {
        WishlistService wishlistService = APIUtils.getRetrofit().create(WishlistService.class);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");
        Call<CustomerWishListTransportBean> call = wishlistService.getWishListProductsForCustomer(String.valueOf(userId), apiToken);
        //Save Products to cart
        call.enqueue(new Callback<CustomerWishListTransportBean>() {
            @Override
            public void onResponse(Call<CustomerWishListTransportBean> call, Response<CustomerWishListTransportBean> response) {
                if (response.isSuccessful()) {
                    CustomerWishListTransportBean customerWishListTransportBeans = response.body();
                    if (customerWishListTransportBeans != null && customerWishListTransportBeans.getProducts().size() > 0) {
                        for (CustomerWishTransportBean customerWishlistTransportBean : customerWishListTransportBeans.getProducts()) {
                            Wishlist wishlist = wishlistViewModel.getWishlistForUserAndProduct(userId, Integer.parseInt(customerWishlistTransportBean.getProductId()));
                            if (wishlist == null) {
                                wishlist = new Wishlist();
                                wishlist.setProductId(Integer.valueOf(customerWishlistTransportBean.getProductId()));
                                wishlist.setUserId(userId);
                                wishlistViewModel.insertWishlist(wishlist);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerWishListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
            }
        });
    }


    private void getAllProducts() {
        ProductService productService = APIUtils.getRetrofit().create(ProductService.class);
        Call<ProductListTransportBean> call = productService.getAllProducts();

        call.enqueue(new Callback<ProductListTransportBean>() {
            @Override
            public void onResponse(Call<ProductListTransportBean> call, Response<ProductListTransportBean> response) {
                if (response.isSuccessful()) {
                    ProductListTransportBean productTransportBeans = response.body();
                    productPresentationBeans = new ArrayList<ProductPresentationBean>();
                    if (productTransportBeans != null && productTransportBeans.getProducts().size() > 0) {
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
                }
            }

            @Override
            public void onFailure(Call<ProductListTransportBean> call, Throwable throwable) {
                Log.e(this.getClass().getName(), throwable.toString());
                // No data is retrieved. Check if there is no internet
                if (!NetworkUtils.isNetworkConnected(getActivity())) {
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
                    return;
                } else { // If there is internet then there is an error retrieving data. display error retrieve message
                    EssentialsUtils.showMessageAlertDialog(getActivity(), ApplicationConstants.DATA_ERROR, ApplicationConstants.ERROR_RETRIEVE_MESSAGE);
                    return;
                }
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
        getProductsForCustomer();
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

    public void filter(String query) {
        adapter.performFilter(query);
        Log.d("TEsting", query);

    }

}

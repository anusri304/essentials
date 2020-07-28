package com.example.essentials.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;
import com.example.essentials.repository.ProductRepository;
import com.example.essentials.repository.UserRepository;

import java.util.List;

@SuppressWarnings("ALL")
public class ProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;
    int productId;
    Product product;
    LiveData<List<Product>> products;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        products = productRepository.getAllProducts();
    }

    public ProductViewModel(@NonNull Application application, int productId) {
        super(application);
        productRepository = new ProductRepository(application);
        product = productRepository.getProduct(productId);
        products = productRepository.getAllProducts();
    }

    public void insertProduct(Product product, Context context, String imageURL){
        productRepository.insertProduct(product,context,imageURL);
    }

    public void updateProduct(Product product,Context context,String imageURL){
        productRepository.updateProduct(product,context,imageURL);
    }
    public Product getProduct(int productId){
        return productRepository.getProduct(productId);
    }

    public LiveData<List<Product>> getAllProducts() {
        return products;
    }

}

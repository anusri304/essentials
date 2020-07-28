package com.example.essentials.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.essentials.dao.ProductDao;
import com.example.essentials.dao.UserDao;
import com.example.essentials.database.EssentialsRoomDatabase;
import com.example.essentials.domain.Product;
import com.example.essentials.domain.User;
import com.example.essentials.executors.AppExecutors;
import com.example.essentials.utils.ApplicationConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class ProductRepository {
    private ProductDao productDao;

    public ProductRepository(Application application) {
        EssentialsRoomDatabase db = EssentialsRoomDatabase.getDatabase(application);
        productDao = db.productDao();
    }


    public void insertProduct(Product product, Context context, String imageURL) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            String imagePath = saveImagetoLocal(context, imageURL);
            Log.d("Anandhi path", String.valueOf(product.getId()));
            product.setImagePath(imagePath);
            productDao.insertProduct(product);
        });
    }

    public void updateProduct(Product product, Context context, String imageURL) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            String imagePath = saveImagetoLocal(context, imageURL);
            //  Log.d("Anandhi update",imagePath);
            product.setImagePath(imagePath);
            productDao.updateProduct(product);
        });
    }

    private String saveImagetoLocal(Context context, String path) {
        OutputStream output = null;
        String filePath = null;
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
        try {
            URL url = new URL(path);
            InputStream input = url.openStream();
            filePath = context.getFilesDir().getAbsolutePath() + fileName;
            File file = new File(filePath);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
        } catch (Exception e) {
            Log.d("Anandhi 12345", ApplicationConstants.ERROR_SAVE_FILE);
        }
        return filePath;
    }


    public Product getProduct(int productId) {

//        AppExecutors.getInstance().diskIO().execute(() -> userDao.getUser(customerId));
//        return userDao.getUser(customerId);
        Product product = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Future<Product> future = executorService.submit(new MyInfoCallable(productId, productDao));
            product = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    private static class MyInfoCallable implements Callable<Product> {

        int productId;
        ProductDao productDao;

        public MyInfoCallable(int productId, ProductDao productDao) {
            this.productId = productId;
            this.productDao = productDao;
        }

        @Override
        public Product call() throws Exception {
            return productDao.getProduct(productId);
        }
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }
}

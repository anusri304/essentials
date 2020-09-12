package com.example.essentials.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.domain.Cart;
import com.example.essentials.service.CartService;
import com.example.essentials.transport.CartTransportBean;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder> {
    List<CartPresentationBean> mValues;
    final Context mContext;
    CartViewModel cartViewModel;
    List<Integer> quantityList = new ArrayList<Integer>();
    ArrayAdapter<Integer> dataAdapter;
    int currentItem = 0;
    CartRecyclerViewAdapter.CartViewHolder holder;
    int check = 0;
    static int selectedPosition = 0;
    private final CartRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    boolean itemSelected = false;


    public interface ListItemClickListener {

        void onListItemClick(CartPresentationBean cartPresentationBean);

    }

    public CartRecyclerViewAdapter(Context context, List<CartPresentationBean> cartItems, CartViewModel cartViewModel,CartRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = cartItems;
        mOnClickListener = listener;
        mContext = context;
        this.cartViewModel = cartViewModel;
        MaterialButton moveToWishListButton;
        for (int i = 1; i <= 1000; i++) {
            quantityList.add(i);
        }

        dataAdapter = new ArrayAdapter<Integer>(mContext,
                android.R.layout.simple_spinner_item, quantityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        MaterialButton moveToWishlistButton;
        Spinner spinner;

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView) itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView) itemView.findViewById(R.id.product_price);
            moveToWishlistButton = (MaterialButton) itemView.findViewById(R.id.move_to_wishlist_button);
            spinner = (Spinner) itemView.findViewById(R.id.qty_spinner);
            moveToWishlistButton.setOnClickListener(this);
            // spinner.setOnItemSelectedListener( this);
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick( mValues.get(clickedPosition));
        }


    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_cart_list, viewGroup, false);
        CartViewHolder holder = new CartRecyclerViewAdapter.CartViewHolder(view);
        holder.spinner.setAdapter(dataAdapter);
        //   holder.spinner.setSelection(selectedPosition, true);
        return holder;
    }

    @Override
    public void onBindViewHolder(CartRecyclerViewAdapter.CartViewHolder holder, int position) {
        //mValues.get(position).getImage()
        holder.imageView.setImageUrl(
                mValues.get(position).getImage(),
                ImageLoaderHelper.getInstance(mContext).getImageLoader());
        holder.imageView.setAspectRatio(ApplicationConstants.ASPECT_RATIO);
        Glide.with(mContext)
                .load(mValues.get(position).getImage())
                //  .load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);
        holder.productNameTxtView.setText(mValues.get(position).getName());
        int quantity = mValues.get(position).getQuantity();
        int productId = mValues.get(position).getProductId();
        double price = Double.valueOf(mValues.get(position).getPrice().substring(1)) * quantity;
        holder.productPriceTxtView.setText(mValues.get(position).getPrice());

//       if(!itemSelected) {
//           holder.spinner.setSelection(quantity - 1, true);
//       }

        holder.spinner.setSelection(quantity - 1, false);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if (++check >= 1) {
                    itemSelected = true;
                    Log.d("Spinner ", parentView.getItemAtPosition(position).toString());
                    //selectedPosition = position;
                  //  holder.spinner.setSelection(selectedPosition, true);
                    callEditCartEndPoint(Integer.valueOf(parentView.getItemAtPosition(position).toString()),productId);
                   // updateCartQuantity(Integer.valueOf(parentView.getItemAtPosition(position).toString()),productId);
                    //notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    private void updateCartQuantity(int quantity, int productId) {
        SharedPreferences pref = mContext.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);

        Cart cart = cartViewModel.getCartItemsForUserAndProduct(userId, productId);
        cart.setQuantity(quantity);
        cartViewModel.updateCartItems(cart);
        //   cartViewModel.getQuantity().setValue(quantity);
//
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void callEditCartEndPoint(int quantity,int productId) {
        SharedPreferences pref = mContext.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0); // 0 - for private mode
        int userId = pref.getInt(ApplicationConstants.USER_ID, 0);
        String apiToken = pref.getString(ApplicationConstants.API_TOKEN, "");

        Log.d("Anandhi userId", String.valueOf(userId));
        Log.d("Anandhi apiToken", apiToken);
        CartService cartService = APIUtils.getRetrofit().create(CartService.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("productId", String.valueOf(productId))
                .addFormDataPart("customerId", String.valueOf(userId))
                .addFormDataPart("quantity", String.valueOf(quantity))
                .build();
        Call<CartTransportBean> call = cartService.editCartItems(apiToken, requestBody);

        call.enqueue(new Callback<CartTransportBean>() {
            @Override
            public void onResponse(Call<CartTransportBean> call, Response<CartTransportBean> response) {
                CartTransportBean cartTransportBean = response.body();
                if(response.isSuccessful()) {
                    updateCartQuantity(quantity,productId);
                }
            }

            @Override
            public void onFailure(Call<CartTransportBean> call, Throwable throwable) {
                APIUtils.getFirebaseCrashlytics().log(ApplicationConstants.FAILED_TO_EDIT_CART_ITEMS);
            }
        });
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<CartPresentationBean> list) {
        mValues.addAll(list);
        notifyDataSetChanged();
    }

}

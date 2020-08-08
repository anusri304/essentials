package com.example.essentials.adapter;

import android.content.Context;
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
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.ApplicationConstants;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>{
    List<ProductPresentationBean> mValues;
    final Context mContext;
    private final CartRecyclerViewAdapter.ItemSelectedListener mOnSelectedListener;

    List<Integer> quantityList = new ArrayList<Integer>();


    public interface ItemSelectedListener {

        void onItemSelected(int quantity,String test);

    }

    public CartRecyclerViewAdapter(Context context, List<ProductPresentationBean> products, CartRecyclerViewAdapter.ItemSelectedListener listener) {
        mValues = products;
        mOnSelectedListener = listener;
        mContext = context;
        for (int i=1; i<=1000; i++){
            quantityList.add(i);
        }
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        MaterialButton addToWishlistButton;
        Spinner spinner;

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView)  itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView)  itemView.findViewById(R.id.product_price);
            addToWishlistButton = (MaterialButton) itemView.findViewById(R.id.add_to_wishlist_button);
            spinner = (Spinner) itemView.findViewById(R.id.qty_spinner);
            spinner.setOnItemSelectedListener( this);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mOnSelectedListener.onItemSelected(Integer.valueOf(spinner.getSelectedItem().toString()),"Test");
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


    }
    @NonNull
    @Override
    public CartRecyclerViewAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_cart_list, viewGroup, false);

        return new CartRecyclerViewAdapter.CartViewHolder(view);
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
        holder.productNameTxtView.setText( mValues.get(position).getName());
        holder.productPriceTxtView.setText( mValues.get(position).getPrice());

        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(mContext,
                android.R.layout.simple_spinner_item, quantityList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(dataAdapter);


//        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//                Log.d("Spinner ",holder.spinner.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

package com.example.essentials.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.ApplicationConstants;

import java.util.List;

public class DeliveryRecyclerViewAdapter extends RecyclerView.Adapter<DeliveryRecyclerViewAdapter.CartViewHolder> {
    List<CartPresentationBean> mValues;
    final Context mContext;
    DeliveryRecyclerViewAdapter.CartViewHolder holder;
    int check = 0;
    static int selectedPosition = 0;
    double price;



    public DeliveryRecyclerViewAdapter(Context context, List<CartPresentationBean> cartItems) {
        mValues = cartItems;
        mContext = context;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        TextView productQtyTxtView;

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView) itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView) itemView.findViewById(R.id.product_price);
            productQtyTxtView = (TextView) itemView.findViewById(R.id.product_Qty);
            // spinner.setOnItemSelectedListener( this);
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
        }


    }

    @NonNull
    @Override
    public DeliveryRecyclerViewAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_delivery_list, viewGroup, false);
        CartViewHolder holder = new DeliveryRecyclerViewAdapter.CartViewHolder(view);
        //   holder.spinner.setSelection(selectedPosition, true);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeliveryRecyclerViewAdapter.CartViewHolder holder, int position) {
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
        int quantity = mValues.get(position).getQuantity();
        holder.productNameTxtView.setText(mValues.get(position).getName());
        if(mValues.get(position).getPrice().substring(1)!=null || !mValues.get(position).getPrice().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)  ) {
            price = Double.valueOf(mValues.get(position).getPrice().substring(1)) * quantity;
        }
        holder.productPriceTxtView.setText(mValues.get(position).getPrice());
        holder.productQtyTxtView.setText( String.valueOf(mValues.get(position).getQuantity()));
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }



}

package com.example.essentials.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;


public class WishlistRecyclerViewAdapter extends RecyclerView.Adapter<WishlistRecyclerViewAdapter.WishlistViewHolder> {

    List<ProductPresentationBean> mValues;
    final Context mContext;
    private final WishlistRecyclerViewAdapter.ListItemClickListener mOnClickListener;
   ProductPresentationBean productPresentationBean;
   int clickedPosition;

    public interface ListItemClickListener {

        void onListItemClick(ProductPresentationBean productPresentationBean);

    }


    public WishlistRecyclerViewAdapter(Context context, List<ProductPresentationBean> products, WishlistRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = products;
        mOnClickListener = listener;
        mContext = context;
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        TextView productSpecialPriceTxtView;
        MaterialButton addToCartButton;

        public WishlistViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView)  itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView)  itemView.findViewById(R.id.product_price);
            productSpecialPriceTxtView = (TextView)  itemView.findViewById(R.id.product_special_price);
            addToCartButton = (MaterialButton) itemView.findViewById(R.id.add_to_cart_button);
            addToCartButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
           int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick( mValues.get(clickedPosition));
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_wishlist_list, viewGroup, false);

        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        //clickedPosition = position;
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
        //todo Special
        if(mValues.get(position).getSpecial().equalsIgnoreCase("")){
            holder.productSpecialPriceTxtView.setVisibility(View.GONE);
        }
        else {
            holder.productPriceTxtView.setPaintFlags(holder.productPriceTxtView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productSpecialPriceTxtView.setText( mValues.get(position).getSpecial());
            holder.productPriceTxtView.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        holder.productNameTxtView.setText( mValues.get(position).getName());
        holder.productPriceTxtView.setText( mValues.get(position).getPrice());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
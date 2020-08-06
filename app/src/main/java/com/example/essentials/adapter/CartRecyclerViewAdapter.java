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

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>{
    List<ProductPresentationBean> mValues;
    final Context mContext;
    private final CartRecyclerViewAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    public CartRecyclerViewAdapter(Context context, List<ProductPresentationBean> products, CartRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = products;
        mOnClickListener = listener;
        mContext = context;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        MaterialButton addToWishlistButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView)  itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView)  itemView.findViewById(R.id.product_price);
            addToWishlistButton = (MaterialButton) itemView.findViewById(R.id.add_to_wishlist_button);
            addToWishlistButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
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

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}

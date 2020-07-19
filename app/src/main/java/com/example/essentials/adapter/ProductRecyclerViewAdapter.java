package com.example.essentials.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.R;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;

import java.util.List;


public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.MovieViewHolder> {

    final List<ProductPresentationBean> mValues;
    final Context mContext;
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    public ProductRecyclerViewAdapter(Context context, List<ProductPresentationBean> values, ListItemClickListener listener) {
        mValues = values;
        mOnClickListener = listener;
        mContext = context;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_product_detail, viewGroup, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.imageView.setImageUrl(
                "http://10.0.75.1/Opencart/image/cache/catalog/demo/apple_cinema_30-228x228.jpg",
                ImageLoaderHelper.getInstance(mContext).getImageLoader());
        holder.imageView.setAspectRatio(1.5f);
        Glide.with(mContext)
              .load("http://10.0.75.1/Opencart/image/cache/catalog/demo/apple_cinema_30-228x228.jpg")
              //  .load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
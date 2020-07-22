package com.example.essentials.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.R;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.MovieViewHolder> {

    List<ProductPresentationBean> mValues;
    final Context mContext;
    private final ListItemClickListener mOnClickListener;

    private final List<ProductPresentationBean> unfilteredProductList;

    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    public ProductRecyclerViewAdapter(Context context, List<ProductPresentationBean> products, ListItemClickListener listener) {
        mValues = products;
        mOnClickListener = listener;
        mContext = context;
        unfilteredProductList = Collections.synchronizedList(new ArrayList<ProductPresentationBean>(products));
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        TextView productSpecialPriceTxtView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView)  itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView)  itemView.findViewById(R.id.product_price);
            productSpecialPriceTxtView = (TextView)  itemView.findViewById(R.id.product_special_price);
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_product_list, viewGroup, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
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


    public Filter getFilter() {
        Filter itemListFilter = new Filter() {
            // Custom filtering
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results;
                try {
                    results = new FilterResults();
                    constraint = constraint.toString().toLowerCase(Locale.getDefault());

                    List<ProductPresentationBean> filteredList = new ArrayList<ProductPresentationBean>();

                    synchronized (unfilteredProductList) {
                        for (ProductPresentationBean productPresentationBean : unfilteredProductList) {
                            if (productPresentationBean.getImage().toLowerCase(Locale.getDefault()).contains(constraint.toString() )) {
                                filteredList.add(productPresentationBean);
                            }
                        }
                    }
                    results.count = filteredList.size();
                    results.values = filteredList;

                    return results;
                } finally {
                    results = null;
                }
            }

            // Publish filtered results
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mValues = (ArrayList<ProductPresentationBean>) results.values;
                notifyDataSetChanged();
                EssentialsUtils.hideKeyboard(mContext);
            }
        };

        return itemListFilter;
    }

    public void performFilter(String query) {
        if (query == null) {
           return;
        }
        getFilter().filter(query);
    }
}
package com.example.essentials.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.ProductDetailActivity;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder> {

    List<ProductPresentationBean> mValues;
    final Context mContext;
    private final List<ProductPresentationBean> unfilteredProductList;


    public ProductRecyclerViewAdapter(Context context, List<ProductPresentationBean> products) {
        mValues = products;
        mContext = context;
        unfilteredProductList = Collections.synchronizedList(new ArrayList<ProductPresentationBean>(products));
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final DynamicHeightNetworkImageView imageView;
        TextView productNameTxtView;
        TextView productPriceTxtView;
        TextView productSpecialPriceTxtView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imageView = (DynamicHeightNetworkImageView) itemView.findViewById(R.id.imageView);
            productNameTxtView = (TextView) itemView.findViewById(R.id.product_name);
            productPriceTxtView = (TextView) itemView.findViewById(R.id.product_price);
            productSpecialPriceTxtView = (TextView) itemView.findViewById(R.id.product_special_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            // mOnClickListener.onListItemClick(mValues.get(clickedPosition));
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, imageView, imageView.getTransitionName()).toBundle();
            Intent intent = new Intent(mContext, ProductDetailActivity.class);
            intent.putExtra(ApplicationConstants.PRODUCT_PRESENTATION_BEAN, (mValues.get(clickedPosition)));
            mContext.startActivity(intent, bundle);
        }
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_product_list, viewGroup, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
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
        if (mValues.get(position).getSpecial().equalsIgnoreCase("")) {
            holder.productSpecialPriceTxtView.setVisibility(View.GONE);
            holder.productPriceTxtView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.productPriceTxtView.setPaintFlags(holder.productPriceTxtView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            holder.productPriceTxtView.setPaintFlags(holder.productPriceTxtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productSpecialPriceTxtView.setText(mValues.get(position).getSpecial());
            holder.productPriceTxtView.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        holder.productNameTxtView.setText(mValues.get(position).getName());
        holder.productPriceTxtView.setText(mValues.get(position).getPrice());

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
                            if (productPresentationBean.getImage().toLowerCase(Locale.getDefault()).contains(constraint.toString())) {
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
                logAnalyticsEvent(constraint.toString());
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


    private void logAnalyticsEvent(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        APIUtils.getFirebaseAnalytics(mContext).logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }
}
package com.example.essentials.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.CategoryPresentationBean;

import java.util.List;


public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder> {

    List<CategoryPresentationBean> mValues;
    final Context mContext;
    private final CategoryRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    CategoryPresentationBean categoryPresentationBean;
    int clickedPosition;

    public interface ListItemClickListener {

        void onListItemClick(CategoryPresentationBean categoryPresentationBean);

    }


    public CategoryRecyclerViewAdapter(Context context, List<CategoryPresentationBean> products, CategoryRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = products;
        mOnClickListener = listener;
        mContext = context;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoryNameTxtView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTxtView = (TextView) itemView.findViewById(R.id.category_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            v.setSelected(true);
            mOnClickListener.onListItemClick(mValues.get(clickedPosition));
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_category_list, viewGroup, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        //clickedPosition = position;
        //mValues.get(position).getImage()
        holder.categoryNameTxtView.setText(mValues.get(position).getName());

        //holder.categoryNameTxtView.setBackgroundColor(Color.LTGRAY);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
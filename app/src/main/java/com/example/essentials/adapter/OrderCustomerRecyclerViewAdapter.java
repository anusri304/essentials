package com.example.essentials.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.OrderCustomerPresentationBean;

import java.util.List;

public class OrderCustomerRecyclerViewAdapter extends RecyclerView.Adapter<OrderCustomerRecyclerViewAdapter.OrderViewHolder> {
    List<OrderCustomerPresentationBean> mValues;
    final Context mContext;
    OrderCustomerRecyclerViewAdapter.OrderViewHolder holder;
    private final OrderCustomerRecyclerViewAdapter.ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {

        void onListItemClick(OrderCustomerPresentationBean cartPresentationBean);

    }

    public OrderCustomerRecyclerViewAdapter(Context context, List<OrderCustomerPresentationBean> orderItems, OrderCustomerRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = orderItems;
        mOnClickListener = listener;
        mContext = context;

    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView orderTxtView;
        ImageView forwardButtonImageView;
        CardView orderCardView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            forwardButtonImageView =  itemView.findViewById(R.id.imageView);
            orderTxtView = (TextView) itemView.findViewById(R.id.order_txt_view);
            orderCardView = (CardView) itemView.findViewById(R.id.orderCardView);
            orderCardView.setOnClickListener(this);
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
    public OrderCustomerRecyclerViewAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_order_list, viewGroup, false);
        OrderViewHolder holder = new OrderCustomerRecyclerViewAdapter.OrderViewHolder(view);
        //   holder.spinner.setSelection(selectedPosition, true);
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderCustomerRecyclerViewAdapter.OrderViewHolder holder, int position) {
        String orderDisplay =mValues.get(position).getDateAdded();
        holder.orderTxtView.setText(orderDisplay);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}

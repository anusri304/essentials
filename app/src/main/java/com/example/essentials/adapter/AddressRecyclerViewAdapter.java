package com.example.essentials.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.DeliveryItemActivity;
import com.example.essentials.activity.bean.AddressPresentationBean;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.ApplicationConstants;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class AddressRecyclerViewAdapter extends RecyclerView.Adapter<AddressRecyclerViewAdapter.AddressViewHolder> {

    List<AddressPresentationBean> mValues;
    final Context mContext;
    private final AddressRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    AddressPresentationBean addressPresentationBean;

    public interface ListItemClickListener {

        void onListItemClick(AddressPresentationBean addressPresentationBean);

    }


    public AddressRecyclerViewAdapter(Context context, List<AddressPresentationBean> address, AddressRecyclerViewAdapter.ListItemClickListener listener) {
        mValues = address;
        mOnClickListener = listener;
        mContext = context;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView addressTxtView;
       Button deleteButton;
       ImageView arrowForward;
       CardView cardView;

        public AddressViewHolder(View itemView) {
            super(itemView);
            addressTxtView = (TextView)  itemView.findViewById(R.id.address_name);
            deleteButton = (Button)  itemView.findViewById(R.id.delete_button);
            arrowForward = (ImageView)  itemView.findViewById(R.id.arrowForward);
            cardView = (CardView) itemView.findViewById(R.id.cardView);

            deleteButton.setOnClickListener(this);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent (mContext, DeliveryItemActivity.class);
                    intent.putExtra(ApplicationConstants.ADDRESS_ID,mValues.get(getAdapterPosition()).getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick( mValues.get(clickedPosition));
        }


    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_address_list, viewGroup, false);

        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        //clickedPosition = position;
        //mValues.get(position).getImage()
        holder.addressTxtView.setText( mValues.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
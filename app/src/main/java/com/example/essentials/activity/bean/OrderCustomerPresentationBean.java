package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class OrderCustomerPresentationBean implements Parcelable {
    private int id;
    private double total;
    private String status;
    private String dateAdded;


    public OrderCustomerPresentationBean(){

    }

    protected OrderCustomerPresentationBean(Parcel in) {
        id = in.readInt();
        total = in.readDouble();
        status = in.readString();
        dateAdded = in.readString();
    }

    public static final Creator<OrderCustomerPresentationBean> CREATOR = new Creator<OrderCustomerPresentationBean>() {
        @Override
        public OrderCustomerPresentationBean createFromParcel(Parcel in) {
            return new OrderCustomerPresentationBean(in);
        }

        @Override
        public OrderCustomerPresentationBean[] newArray(int size) {
            return new OrderCustomerPresentationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeDouble(total);
        dest.writeString(status);
        dest.writeString(dateAdded);
    }
}

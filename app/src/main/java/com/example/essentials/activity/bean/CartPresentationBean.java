package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class CartPresentationBean implements Parcelable {
    private int id;
    private int productId;
    private String image;
    private String name;
    private String price;
    int quantity;

    public CartPresentationBean(){

    }

    protected CartPresentationBean(Parcel in) {
        id = in.readInt();
        productId = in.readInt();
        image = in.readString();
        name = in.readString();
        price = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<CartPresentationBean> CREATOR = new Creator<CartPresentationBean>() {
        @Override
        public CartPresentationBean createFromParcel(Parcel in) {
            return new CartPresentationBean(in);
        }

        @Override
        public CartPresentationBean[] newArray(int size) {
            return new CartPresentationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeInt(productId);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(quantity);
    }
}

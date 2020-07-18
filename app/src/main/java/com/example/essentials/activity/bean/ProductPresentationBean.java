package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class ProductPresentationBean implements Parcelable {
    private int id;
    private String image;

    public ProductPresentationBean() {

    }


    private ProductPresentationBean(Parcel in) {
        id = in.readInt();
        image = in.readString();
    }

    public static final Creator<ProductPresentationBean> CREATOR = new Creator<ProductPresentationBean>() {
        @Override
        public ProductPresentationBean createFromParcel(Parcel in) {
            return new ProductPresentationBean(in);
        }

        @Override
        public ProductPresentationBean[] newArray(int size) {
            return new ProductPresentationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeString(image);
    }
}

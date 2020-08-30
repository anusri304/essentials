package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class ProductPresentationBean implements Parcelable {
    private int id;
    private int categoryId;
    private String image;
    private String name;
    private String description;
    private String price;
    private String special;
    private String discPerc;
    private String inStock;

    public ProductPresentationBean() {

    }


    private ProductPresentationBean(Parcel in) {
        id = in.readInt();
        categoryId = in.readInt();
        image = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        special = in.readString();
        discPerc = in.readString();
        inStock = in.readString();
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
        dest.writeInt(categoryId);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(special);
        dest.writeString(discPerc);
        dest.writeString(inStock);
    }
}

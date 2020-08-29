package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class CategoryPresentationBean implements Parcelable {
    private int id;
    private String name;


    public CategoryPresentationBean(){

    }

    protected CategoryPresentationBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<CategoryPresentationBean> CREATOR = new Creator<CategoryPresentationBean>() {
        @Override
        public CategoryPresentationBean createFromParcel(Parcel in) {
            return new CategoryPresentationBean(in);
        }

        @Override
        public CategoryPresentationBean[] newArray(int size) {
            return new CategoryPresentationBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}

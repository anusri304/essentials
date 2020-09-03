package com.example.essentials.activity.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class AddressPresentationBean implements Parcelable {
    private int id;
    private String name;

    public AddressPresentationBean() {

    }


    private AddressPresentationBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<AddressPresentationBean> CREATOR = new Creator<AddressPresentationBean>() {
        @Override
        public AddressPresentationBean createFromParcel(Parcel in) {
            return new AddressPresentationBean(in);
        }

        @Override
        public AddressPresentationBean[] newArray(int size) {
            return new AddressPresentationBean[size];
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

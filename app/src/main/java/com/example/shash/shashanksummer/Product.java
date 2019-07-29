package com.example.shash.shashanksummer;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    public String start,end,desc,contact,link,ecat;

    public Product(String start, String end, String desc, String contact, String link,String ecat) {
        this.start = start;
        this.end = end;
        this.desc = desc;
        this.contact = contact;
        this.link = link;
        this.ecat = ecat;
    }

    protected Product(Parcel in) {
        start = in.readString();
        end = in.readString();
        desc = in.readString();
        contact = in.readString();
        link = in.readString();
        ecat = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(desc);
        dest.writeString(contact);
        dest.writeString(link);
        dest.writeString(ecat);
    }
}

package com.example.shash.shashanksummer;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Company extends ExpandableGroup<Product> {
    String catego;
    public Company(String title, List<Product> items,String catego) {
        super(title, items);
        this.catego=catego;
    }


}

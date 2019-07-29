package com.example.shash.shashanksummer.Adapters;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shash.shashanksummer.AttendingEvents;
import com.example.shash.shashanksummer.MyEvents;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment =null;

        if(i==0) {
            fragment = new AttendingEvents();
            return fragment;
        }
        if(i==1){
           fragment=new MyEvents();
            return fragment;
        }

        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        if(i==0) {
            return "Attending";
        }
        if(i==1){
            return "My events";

        }
        else{
            return null;
        }

    }
}

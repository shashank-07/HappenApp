package com.example.shash.shashanksummer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shash.shashanksummer.MapsActivity;
import com.example.shash.shashanksummer.MyData;
import com.example.shash.shashanksummer.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mView;
    private final Context mContext;
    public static  String mcat;
    Button checkedin;
    int x=1;
    FirebaseDatabase mdatabase;
    FirebaseAuth mAuth;
    TextView ename,ecat;


    public CustomInfoWindowAdapter( Context mContext) {
       mView=LayoutInflater.from(mContext).inflate(R.layout.eventmarkerinfo,null);
       this.mContext = mContext;
    }
    void searchMarkers(final Marker marker, View view){
        ecat=view.findViewById(R.id.Category);
        ecat.setText(marker.getTitle());

    }




    @Override
    public View getInfoWindow(Marker marker) {
      return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        searchMarkers(marker,mView);

            return mView;


    }

}

package com.example.shash.shashanksummer;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetsFrag extends BottomSheetDialogFragment {

    TextView ecat,ename,edesc,estart,eend,eurl,eco;
    FirebaseAuth mAuth;
    Button attending,fullsend;
    double elat,elong;
    LatLng loc;
    ConstraintLayout ParentConstraint;
    ProgressBar progressBar;
    String sending;
    String url;

    Context context;

    public EventDetsFrag(double x,double y) {
        elat=x;
        elong=y;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.event_details, container, false);
        attending=v.findViewById(R.id.button);
        context=getActivity();
        eurl=v.findViewById(R.id.Link);
        eco=v.findViewById(R.id.PhoneNo);

        ParentConstraint=v.findViewById(R.id.Parentconstraint);
        loc=new LatLng(elat,elong);
        updateText(loc,v);
        fullsend=v.findViewById(R.id.message);
        fullsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.isSignout=true;
                Intent x=new Intent(context,MessageActivity.class);
                x.putExtra("Host",sending);
                startActivity(x);

            }
        });

        attending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGuy(loc);


            }
        });
        eco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.isSignout=true;
                String phone = "+91"+eco.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });
        eurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eurl.getText().toString().charAt(0)=='w'){
                    url="https://"+eurl.getText().toString();
                }
                else{
                    url=eurl.getText().toString();
                }

                MapsActivity.isSignout=true;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

    return v;
    }
    void addGuy(final LatLng location){
        mAuth=FirebaseAuth.getInstance();

        final String user_id=mAuth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot p : dataSnapshot.getChildren()) {
                            if(!p.getKey().matches(user_id)) {
                                if (p.child("Events").hasChildren()) {
                                    for (DataSnapshot d : p.child("Events").getChildren()) {
                                        if (location.latitude == d.child("lat").getValue(Double.class) && location.longitude == d.child("long").getValue(Double.class)) {
                                            DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users").child(p.getKey()).child("Events").child(d.getKey()).child("attendees");
                                            db.child(user_id).push();
                                            db.child(user_id).setValue(true);
                                            Toast.makeText(getActivity(),"You are added ",Toast.LENGTH_SHORT).show();





                                        }

                                    }
                                }
                            }
                            else{
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void updateText(final LatLng loc,View v){


        ename=v.findViewById(R.id.Name);
        ecat=v.findViewById(R.id.Category);
        eurl=v.findViewById(R.id.Link);
        eco=v.findViewById(R.id.PhoneNo);
        estart=v.findViewById(R.id.Start);
        eend=v.findViewById(R.id.End);
        edesc=v.findViewById(R.id.Description);
        attending=v.findViewById(R.id.button);

        mAuth=FirebaseAuth.getInstance();
        final String user_id=mAuth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot p : dataSnapshot.getChildren()) {

                            if (p.child("Events").hasChildren()) {
                                for (DataSnapshot d : p.child("Events").getChildren()) {
                                    if (loc.latitude == d.child("lat").getValue(Double.class) && loc.longitude == d.child("long").getValue(Double.class)) {
                                        if(p.getKey().matches(user_id)){
                                            Log.d("Setting visibility","0");
                                            attending.setVisibility(View.INVISIBLE);
                                        }
                                        else{
                                            Log.d("Setting visibility","1");

                                        }
                                        sending=p.getKey();
                                        ename.setText(d.child("Name").getValue(String.class));
                                        ecat.setText(d.child("Category").getValue(String.class));
                                        eurl.setText(d.child("URL").getValue(String.class));
                                        eco.setText(d.child("Contact Number").getValue(String.class));
                                        estart.setText(d.child("Event start").getValue(String.class));
                                        eend.setText(d.child("Event end").getValue(String.class));
                                        edesc.setText(d.child("Description").getValue(String.class));



                                    }

                                }
                            }

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }





}

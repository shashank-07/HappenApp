package com.example.shash.shashanksummer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Eventdetails extends AppCompatActivity {
    TextView  ecat,ename,edesc,estart,eend,eurl,eco;
    FirebaseAuth mAuth;
    Button attending,fullsend;
    double elat,elong;
    LatLng loc;
    ConstraintLayout constraintLayout,ParentConstraint;
    ProgressBar progressBar;
   String sending;

    Context context;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        attending=findViewById(R.id.button);
        context=this;
        ParentConstraint=findViewById(R.id.Parentconstraint);
        ParentConstraint.setBackgroundResource(MapsActivity.c);
        constraintLayout=findViewById(R.id.constraint);
        constraintLayout.setVisibility(View.INVISIBLE);
        constraintLayout.setBackgroundResource(0);
        Bundle extras = getIntent().getExtras();
        elat=extras.getDouble("MarkerLat");
        elong=extras.getDouble("MarkerLong");
        loc=new LatLng(elat,elong);
        updateText(loc);
        fullsend=findViewById(R.id.message);
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
                Intent intent1=new Intent(Eventdetails.this, CreateActivity.class);
                startActivity(intent1);
                finish();

            }
        });
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

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




                                        }

                                    }
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"You should attend your own event!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void updateText(final LatLng loc){
        constraintLayout=findViewById(R.id.constraint);


        ename=findViewById(R.id.Name);
        ecat=findViewById(R.id.Category);
        eurl=findViewById(R.id.Link);
        eco=findViewById(R.id.PhoneNo);
        estart=findViewById(R.id.Start);
        eend=findViewById(R.id.End);
        edesc=findViewById(R.id.Description);
        attending=findViewById(R.id.button);

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
                                            constraintLayout.setVisibility(View.VISIBLE);



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

    @Override
    protected void onStop() {
        super.onStop();
    }
}









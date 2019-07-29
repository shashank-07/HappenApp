package com.example.shash.shashanksummer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.MimeTypeFilter;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateActivity extends FragmentActivity implements OnMapReadyCallback {
        BottomNavigationView bottomNavigationView;
        private Boolean isSignout = false;
        FusedLocationProviderClient mFusedLocationClient;
        SupportMapFragment mapFragment;
        Location mLastLocation;
        int m=1;
        int i=0;
        FirebaseAuth mAuth;
        LocationRequest mLocationRequest;
        DatabaseReference db;
        EditText ecat,ename,edesc,estart,eend,eco,eurl;
        Boolean isFill=false;
        LatLng location=new LatLng(0,0);
        Button enter,addIm;
        Date xx;
        String Catego;
        private ImageView img;
        public Uri imguri;
        StorageReference mStorageRef;
    CustomDateTimePicker custom,custom1;


    private GoogleMap mMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create);
            bottomNavigationView = findViewById(R.id.nav_view_bar);

            bottomNavigationView.setBackgroundResource(MapsActivity.c);

            Menu menu=bottomNavigationView.getMenu();
            MenuItem menuItem=menu.getItem(1);
            menuItem.setChecked(true);
            mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        ecat=findViewById(R.id.OtherCat);
            ename=findViewById(R.id.Name);
            eend=findViewById(R.id.End);
            estart=findViewById(R.id.Start);
            enter=findViewById(R.id.enter_event);
            final Spinner spinner=findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.months,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Catego=parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(getApplicationContext(),"nuttin",Toast.LENGTH_SHORT).show();


                }
            });
            spinner.setBackgroundResource(R.drawable.mustfill);
            spinner.setFocusableInTouchMode(true);
            spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (spinner.getWindowToken() != null) {
                            spinner.performClick();
                        }
                    }
                }
            });
            mAuth=FirebaseAuth.getInstance();
            String user_id=mAuth.getCurrentUser().getUid();
            db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Events");
            custom = new CustomDateTimePicker(this,
                    new CustomDateTimePicker.ICustomDateTimeListener() {

                        @Override
                        public void onSet(Dialog dialog, Calendar calendarSelected,
                                          Date dateSelected, int year, String monthFullName,
                                          String monthShortName, int monthNumber, int day,
                                          String weekDayFullName, String weekDayShortName,
                                          int hour24, int hour12, int min, int sec,
                                          String AM_PM) {
                            //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
                            estart.setText("");
                            estart.setText(year
                                    + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                                    + " " + hour24 + ":" + min
                                    + ":" + sec);


                        }

                        @Override
                        public void onCancel() {

                        }
                    });

            custom.set24HourFormat(true);
            custom.setDate(Calendar.getInstance());
            addIm=findViewById(R.id.enter_image);
            addIm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(CreateActivity.this,CreateActivity.class);
                    isSignout=true;
                    startActivity(intent);
                }
            });
            estart.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            custom.showDialog();
                        }
                    });
            custom1 = new CustomDateTimePicker(this,
                    new CustomDateTimePicker.ICustomDateTimeListener() {

                        @Override
                        public void onSet(Dialog dialog, Calendar calendarSelected,
                                          Date dateSelected, int year, String monthFullName,
                                          String monthShortName, int monthNumber, int day,
                                          String weekDayFullName, String weekDayShortName,
                                          int hour24, int hour12, int min, int sec,
                                          String AM_PM) {
                            //                        ((TextInputEditText) findViewById(R.id.edtEventDateTime))
                            eend.setText("");
                            eend.setText(year
                                    + "-" + (monthNumber + 1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)
                                    + " " + hour24 + ":" + min
                                    + ":" + sec);


                        }

                        @Override
                        public void onCancel() {

                        }
                    });

            custom1.set24HourFormat(true);
            custom1.setDate(Calendar.getInstance());
            eend.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            custom1.showDialog();
                        }
                    });



            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.showEvent:
                            Intent intent1 = new Intent(CreateActivity.this,MapsActivity.class);
                            startActivity(intent1);
                                          isSignout = true;
                        break;

                        case R.id.createEvent:
                            break;

                        case R.id.myEvent:
                            Intent intent2 = new Intent(CreateActivity.this, MyActivity.class);
                            startActivity(intent2);
                            isSignout = true;
                            break;


                        case R.id.myMessages:
                            Intent intent4 = new Intent(CreateActivity.this,texts.class);
                            startActivity(intent4);
                            isSignout = true;
                            break;

                        case R.id.logoutEvent:
                            isSignout = true;
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                            startActivity(intent);

                            break;

                    }
                    return false;
                }
            });
            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterEvent(location);
                    if(isFill){
                        Toast.makeText(getApplicationContext(),"Event created",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(CreateActivity.this,MapsActivity.class);
                        startActivity(intent1);
                        isSignout = true;

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please Specify location and fill in required boxes",Toast.LENGTH_SHORT).show();

                    }
                }
            });


            initMap();
        }


    public void initMap(){

            mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
            mapFragment= (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        private void enterEvent(LatLng latLng) {
            mAuth=FirebaseAuth.getInstance();
            String user_id=mAuth.getCurrentUser().getUid();
            String catt="lo";
            ecat=findViewById(R.id.OtherCat);
            ename=findViewById(R.id.Name);
            edesc=findViewById(R.id.Description);
            eend=findViewById(R.id.End);
            estart=findViewById(R.id.Start);
            eco=findViewById(R.id.PhoneNo);
            eurl=findViewById(R.id.Link);

           if(!ename.getText().toString().isEmpty()&&!estart.getText().toString().isEmpty()&&!eend.getText().toString().isEmpty()){
               if(Catego.matches("Other")){
                   if(!ecat.getText().toString().isEmpty()){
                       isFill=true;
                      catt="Other-"+ecat.getText().toString();

                   }
                   else{
                       isFill=false;
                   }
               }
               else if(Catego.matches("Choose Category")){
                   isFill=false;

               }
               else{
                   isFill=true;
                   catt=Catego;
               }

           }
           if(latLng.latitude==0){
               isFill=false;
           }

            if(isFill) {

                db.child(catt+"-"+ename.getText().toString()).push();
                db.child(catt+"-"+ename.getText().toString()).setValue(true);
                DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Events").child(catt+"-"+ename.getText().toString());
                mDatabase.child("Creator").push();
                mDatabase.child("Category").push();
                mDatabase.child("URL").push();
                mDatabase.child("Name").push();
                mDatabase.child("Description").push();
                mDatabase.child("Event start").push();
                mDatabase.child("Event end").push();
                mDatabase.child("Contact Number").push();
                mDatabase.child("lat").push();
                mDatabase.child("long").push();
                mDatabase.child("attendees").push();

                mDatabase.child("Creator").setValue(user_id);
                mDatabase.child("URL").setValue(eurl.getText().toString());
                mDatabase.child("Category").setValue(catt);
                mDatabase.child("lat").setValue(latLng.latitude);
                mDatabase.child("long").setValue(latLng.longitude);
                mDatabase.child("Event start").setValue(estart.getText().toString());
                mDatabase.child("Event end").setValue(eend.getText().toString());
                mDatabase.child("Name").setValue(ename.getText().toString());
                mDatabase.child("Description").setValue(edesc.getText().toString());
                mDatabase.child("Contact Number").setValue(eco.getText().toString());
                mDatabase.child("attendees").setValue(true);



            }





        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    mMap.clear();

                        mMap.addMarker(new MarkerOptions()
                                .position(point)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                        );
                        location=point;




                }
            });


            mLocationRequest=new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


                }else{
                    checkLocationPermission();
                }
            }
        }

        LocationCallback mLocationCallback=new LocationCallback(){
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    if(getApplicationContext()!=null){
                        mLastLocation = location;


                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        if(i==0) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                            i=i+1;
                        }

                    }
                }
            }
        };

        private void checkLocationPermission() {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    new AlertDialog.Builder(this)
                            .setTitle("give permission")
                            .setMessage("give permission")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                                }
                            })
                            .create()
                            .show();
                }
                else{
                    ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

                }
            }
        }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch(requestCode){
                case 1:{
                    if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                            mMap.setMyLocationEnabled(true);
                        }
                    } else{
                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }





        }
        @Override
        protected void onStop() {
            if (isSignout) {
                super.onStop();
            }

        }
    }





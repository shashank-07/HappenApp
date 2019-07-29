package com.example.shash.shashanksummer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.renderscript.RenderScript;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.shash.shashanksummer.Adapters.CustomInfoWindowAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.shash.shashanksummer.Constants.Default_Event;
import static com.example.shash.shashanksummer.Constants.Default_Lat;
import static java.sql.Types.NULL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private DatabaseReference mDatabase;
    public static Map<Marker, MyData> allMarkersMap = new HashMap<Marker, MyData>();
    MyData myData;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    ImageButton search;
    FusedLocationProviderClient mFusedLocationClient;
    double lati,longi;
    public static Boolean isSignout=false;
    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    Date currentTime;
    Toolbar toolbar;
    AutoCompleteTextView spinner;
    Spinner autocomplete;
    MaterialSearchView materialSearchView;
    int i=0;
     ArrayList<String> names;
    String eventselec;
    ImageView cross;

    public static int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);


        bottomNavigationView=findViewById(R.id.nav_view_bar);
        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.showEvent:
                        break;

                    case R.id.createEvent:
                        Intent intent1=new Intent(MapsActivity.this,CreateActivity.class);
                        startActivity(intent1);
                        isSignout=true;

                        break;

                    case R.id.myEvent:
                        Intent intent2=new Intent(MapsActivity.this,MyActivity.class);
                        startActivity(intent2);
                        isSignout=true;

                        break;
                    case R.id.myMessages:
                        Intent intent4 = new Intent(MapsActivity.this,texts.class);
                        startActivity(intent4);
                        isSignout = true;
                        break;


                    case R.id.logoutEvent:
                        isSignout=true;
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(MapsActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });


        mAuth=FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
removeMarks();
        initMap();

    }

    public void initMap(){

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
        mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        bottomNavigationView=findViewById(R.id.nav_view_bar);
        mMap = googleMap;
        googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));
        showMarkers();
        search=findViewById(R.id.imageButton);
        spinner = (AutoCompleteTextView) findViewById(R.id.spinner);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMarkers();
            }
        });
        autocomplete = (Spinner) findViewById(R.id.autoComplete);
        ArrayAdapter<CharSequence> dataAdapter1=ArrayAdapter.createFromResource(this,R.array.months,android.R.layout.simple_spinner_item);
        cross=findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete.performClick();
            }
        });
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autocomplete.setAdapter(dataAdapter1);
        autocomplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventselec=parent.getItemAtPosition(position).toString();
                if(eventselec.matches("Party")){
                    c=R.color.Party;
                    int i=c;
                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    cross.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);


                }
                if(eventselec.matches("Group Study")){
                    c=R.color.groupstudy;

                    int i=c;
                    cross.setBackgroundResource(i);

                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);


                }if(eventselec.matches("Club meeting")){
                    c=R.color.ClubMeet;
                    int i=c;
                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);
                    cross.setBackgroundResource(i);


                }if(eventselec.matches("Guest lecture")){
                    c=R.color.guestlec;
                    int i=c;

                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);
                    cross.setBackgroundResource(i);


                }if(eventselec.matches("Fest event")){
                    c=R.color.fest;
                    int i=c;
                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);
                    cross.setBackgroundResource(i);


                }if(eventselec.matches("Other")){
                    c=R.color.other;
                    int i=c;
                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    bottomNavigationView.setBackgroundResource(i);
                    cross.setBackgroundResource(i);



                }
                if(eventselec.matches("Choose Category")){
                    c=R.color.White;
                    int i=c;
                    autocomplete.setBackgroundResource(i);
                    spinner.setBackgroundResource(i);
                    search.setBackgroundResource(i);
                    cross.setBackgroundResource(i);

                    bottomNavigationView.setBackgroundResource(i);
                }

                spinner.setText("");
                showMarkers();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                EventDetsFrag bottomSheet=new EventDetsFrag(marker.getPosition().latitude,marker.getPosition().longitude);
                bottomSheet.show(getSupportFragmentManager(),"example");
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            private static final String TAG = "llol";

            @Override
            public boolean onMarkerClick(Marker marker) {


                try{


                }catch (NullPointerException e){

                    Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                    Toast.makeText(getApplicationContext(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                }


                return false;
            }
        });

        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        i=i+1;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


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
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
                                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

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


    private void connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }


    void showMarkers(){
        mMap.clear();
        spinner=findViewById(R.id.spinner);
         names=new ArrayList<String>();
        final String user_id=mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot p : dataSnapshot.getChildren()) {
                            if(!p.getKey().matches(user_id)){

                            if (p.child("Events").hasChildren()) {
                                for (DataSnapshot d : p.child("Events").getChildren()) {
                                    if (spinner.getText().toString().isEmpty()) {
                                        char x = d.getKey().charAt(0);
                                        if (d.child("Category").getValue(String.class).matches(eventselec)) {

                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();

                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (eventselec.matches("Choose Category")) {
                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();

                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (x == 'O' && eventselec.matches("Other")) {
                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();
                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } else if (spinner.getText().toString().toLowerCase().matches(d.child("Name").getValue(String.class).toLowerCase())) {
                                        char x = d.getKey().charAt(0);
                                        if (d.child("Category").getValue(String.class).matches(eventselec)) {

                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();

                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (eventselec.matches("Choose Category")) {
                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();

                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (x == 'O' && eventselec.matches("Other")) {
                                            LatLng location = new LatLng(d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");

                                            currentTime = Calendar.getInstance().getTime();
                                            try {
                                                Date eventStart = format.parse(d.child("Event start").getValue(String.class));

                                                Log.d("shashankK", currentTime.toString() + "   " + eventStart.toString());
                                                Marker marker;
                                                if (d.child("Category").getValue(String.class).matches("Party")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                } else if (d.child("Category").getValue(String.class).matches("Group Study")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                } else if (d.child("Category").getValue(String.class).matches("Club meeting")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                                } else if (d.child("Category").getValue(String.class).matches("Guest lecture")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                                                } else if (d.child("Category").getValue(String.class).matches("Fest event")) {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                                                } else {
                                                    marker = mMap.addMarker(new MarkerOptions().position(location).title(d.child("Name").getValue(String.class)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                                names.add(d.child("Name").getValue(String.class));
                                                myData = new MyData(d.child("Category").getValue(String.class), d.child("Name").getValue(String.class), d.child("Description").getValue(String.class), d.child("Event Start").getValue(String.class), d.child("Event end").getValue(String.class), d.child("URL").getValue(String.class), d.child("Contact Number").getValue(String.class), d.child("lat").getValue(Double.class), d.child("long").getValue(Double.class));

                                                allMarkersMap.put(marker, myData);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }


                                }
                            }
                        }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        spinner = (AutoCompleteTextView) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }
    private void removeMarks(){
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot d:dataSnapshot.child("Events").getChildren()){
                   SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
                   try {
                       Date eventStart = format.parse(d.child("Event start").getValue(String.class));
                       Date a=format.parse(Calendar.getInstance().getTime().toString());
                       if(a.after(eventStart)){
                           FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.getKey()).child("Events").child(d.getKey()).setValue(null);
                       }
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        i=0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        i=0;
    }

    @Override
    protected void onStop() {
        if(isSignout) {
            super.onStop();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

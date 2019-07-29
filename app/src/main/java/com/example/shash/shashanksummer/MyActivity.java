package com.example.shash.shashanksummer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.example.shash.shashanksummer.Adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MyActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Boolean isSignout = false;
    RecyclerView recyclerView;
    ImageView arrow;
    private Toolbar toolbar;
    StringBuilder stringBuilder;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager=findViewById(R.id.pager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout=findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        bottomNavigationView = findViewById(R.id.nav_view_bar);
        bottomNavigationView.setBackgroundResource(MapsActivity.c);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.showEvent:
                        Intent intent1 = new Intent(MyActivity.this,MapsActivity.class);
                        startActivity(intent1);
                        isSignout = true;
                        break;

                    case R.id.createEvent:
                        Intent intent2 = new Intent(MyActivity.this, CreateActivity.class);
                        startActivity(intent2);
                        isSignout = true;
                        break;

                    case R.id.myEvent:

                        break;
                    case R.id.myMessages:
                        Intent intent4 = new Intent(MyActivity.this,texts.class);
                        startActivity(intent4);
                        isSignout = true;
                        break;


                    case R.id.logoutEvent:
                        isSignout = true;
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MyActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });




    }



    @Override
    protected void onStop() {
        if (isSignout) {
            super.onStop();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}



//
package com.example.shash.shashanksummer;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendingEvents extends Fragment {
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    ArrayList<Company> companies=new ArrayList<>();
    ProductAdapter adapter;

    public AttendingEvents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_my_events, container, false);
        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth=FirebaseAuth.getInstance();


        final String user_id=mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot p : dataSnapshot.getChildren()){
                    if(!p.getKey().matches(user_id)){

                        if(p.child("Events").hasChildren()){
                            for(DataSnapshot d: p.child("Events").getChildren()){
                                ArrayList<Product>googleproduct=new ArrayList<>();
                                if(d.child("attendees").hasChild(user_id)) {

                                    googleproduct.add(new Product(d.child("Event start").getValue(String.class),d.child("Event end").getValue(String.class),d.child("Description").getValue(String.class),d.child("Contact Number").getValue(String.class),d.child("URL").getValue(String.class),d.getKey()));
                                    Company google = new Company(d.getKey(), googleproduct,d.child("Category").getValue(String.class));
                                    companies.add(google);
                                }
                                continue;

                            }
                            adapter=new ProductAdapter(companies);

                            recyclerView.setAdapter(adapter);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;

    }



}

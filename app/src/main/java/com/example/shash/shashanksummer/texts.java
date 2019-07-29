package com.example.shash.shashanksummer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shash.shashanksummer.Adapters.MessageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class texts extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Boolean isSignout = false;
    private FirebaseAuth mAuth;
    String path;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texts);
        background();
        checkExist();

    }



    private void checkExist() {
        mAuth=FirebaseAuth.getInstance();
        final String user_id=mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("sms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot d) {
                if(d.hasChildren()) {
                    ArrayList<Message> messages=new ArrayList<>();
                    final ArrayList<String> sending=new ArrayList<>();

                    for(DataSnapshot e:d.getChildren()){

                        if (e.getKey().substring(0, 28).matches(user_id)) {
                           long x=e.getChildrenCount();
                           for(DataSnapshot f:e.getChildren()){
                              path=f.getKey();
                           }
                           Message as=new Message(e.child(path).child("message").getValue(String.class),e.child(path).child("user").getValue(String.class));
                           messages.add(as);
                           sending.add(e.getKey().substring(29,57));
                        }
                        else if (e.getKey().substring(29, 57).matches(user_id)) {
                            for(DataSnapshot f:e.getChildren()){
                                path=f.getKey();
                            }
                            Message as=new Message(e.child(path).child("message").getValue(String.class),e.child(path).child("user").getValue(String.class));
                            messages.add(as);
                            sending.add(e.getKey().substring(0,28));


                        }

                    }
                    listView=findViewById(R.id.list);
                    MessageAdapter adapter=new MessageAdapter(getApplicationContext(),0,messages);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            isSignout=true;
                            Intent x=new Intent(texts.this,MessageActivity.class);
                            x.putExtra("Host",sending.get(position));
                            startActivity(x);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void background(){
        bottomNavigationView = findViewById(R.id.nav_view_bar);
        bottomNavigationView.setBackgroundResource(MapsActivity.c);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.showEvent:
                        Intent intent1 = new Intent(texts.this,MapsActivity.class);
                        startActivity(intent1);
                        isSignout = true;
                        break;

                    case R.id.createEvent:
                        Intent intent2 = new Intent(texts.this, CreateActivity.class);
                        startActivity(intent2);
                        isSignout = true;
                        break;

                    case R.id.myEvent:
                        Intent intent3 = new Intent(texts.this, MyActivity.class);
                        startActivity(intent3);
                        isSignout = true;
                        break;
                    case R.id.myMessages:
                        break;


                    case R.id.logoutEvent:
                        isSignout = true;
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(texts.this, MainActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });
    }
    public interface MyCallback {
        void onCallback(String value);
    }
    public void readData(final MessageActivity.MyCallback myCallback,String user_id) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value=dataSnapshot.getValue(String.class);
                myCallback.onCallback(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStop() {
        if (isSignout) {
            super.onStop();
        }

    }

}

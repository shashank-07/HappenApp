package com.example.shash.shashanksummer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shash.shashanksummer.Adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    long currentTime;
    private EditText mMessageEditText;
    private Button mSendButton;
    String path;
    private FirebaseAuth mAuth;
    String sending;
    ListView listView;
    TextView reciever;
   String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mAuth=FirebaseAuth.getInstance();
        Bundle extras = getIntent().getExtras();
       sending=extras.getString("Host");
       reciever=findViewById(R.id.reciever);
       reciever.setText(sending);
        final String user_id=mAuth.getCurrentUser().getUid();

        checkExist(sending);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(mMessageEditText.getText().toString());
               sendOnce(mMessageEditText.getText().toString());
                mMessageEditText.setText("");

            }
        });
    }
private void sendOnce(final String s){
    readData(new MyCallback() {

        public void onCallback(final String value) {
            updateText(s,value);
        }
    });
}
    private void updateText(final String s,final String u){

        FirebaseDatabase.getInstance().getReference().child("sms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot d) {
                ArrayList<Message> messages=new ArrayList<>();


                    for (DataSnapshot db : d.child(path).getChildren()) {
                        Message x = new Message(db.child("message").getValue(String.class),db.child("user").getValue(String.class));
                        Log.d("asdfeg", "  " + x.getDate() + "    " + x.getText());
                        messages.add(x);

                    }
                    if(!s.isEmpty()) {
                        Message y = new Message(s, u);

                        messages.add(y);
                    }


                listView=findViewById(R.id.messageList);
                MessageAdapter adapter=new MessageAdapter(getApplicationContext(),0,messages);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage(final String message) {
        final String user_id=mAuth.getCurrentUser().getUid();

        currentTime = Calendar.getInstance().getTimeInMillis();
        FirebaseDatabase.getInstance().getReference().child("sms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                readData(new MyCallback() {

                    public void onCallback(final String value) {

                DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("sms").child(path);

                mDatabase.child(String.valueOf(currentTime)).push();
                mDatabase.child(String.valueOf(currentTime)).setValue(true);
                DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("sms").child(path).child(String.valueOf(currentTime));
                db.child("user").push();
                db.child("message").push();
                db.child("user").setValue(value);
                db.child("message").setValue(message);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkExist(final String sending) {
        mAuth=FirebaseAuth.getInstance();
        final String user_id=mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("sms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot d) {

                if(d.hasChild(user_id+"-"+sending)){
                    path=user_id+"-"+sending;
                    updateText("","");

                }
                else if(d.hasChild(sending+"-"+user_id)){
                    path=sending+"-"+user_id;
                    updateText("","");
                }
                else{
                    path=user_id+"-"+sending;
                    DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("sms");
                    mDatabase.child(path).push();
                    mDatabase.child(path).setValue(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public interface MyCallback {
        void onCallback(String value);
    }
    public void readData(final MyCallback myCallback) {
        final String user_id=mAuth.getCurrentUser().getUid();
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


}


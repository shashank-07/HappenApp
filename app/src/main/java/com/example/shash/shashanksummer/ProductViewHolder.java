package com.example.shash.shashanksummer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class ProductViewHolder extends ChildViewHolder {
    private EditText desc,contact,link;
    private TextView start,end;
    FirebaseAuth mAuth;
    private Button savedets;
    Context context;
    public ProductViewHolder(View itemView) {
        super(itemView);

        savedets=itemView.findViewById(R.id.button);
       start=itemView.findViewById(R.id.Start);
       end=itemView.findViewById(R.id.End);
       desc=itemView.findViewById(R.id.Description);
       contact=itemView.findViewById(R.id.PhoneNo);
       link=itemView.findViewById(R.id.Link);
    }
    public void bind(final Product product){

        mAuth= FirebaseAuth.getInstance();
        final String user_id=mAuth.getCurrentUser().getUid();
       start.setText(product.start);
       end.setText(product.end);
        desc.setText(product.desc);
        contact.setText(product.contact);
        link.setText(product.link);
       savedets.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Events").child(product.ecat);

               mDatabase.child("URL").setValue(link.getText().toString());
               mDatabase.child("Description").setValue(desc.getText().toString());
               mDatabase.child("Contact Number").setValue(contact.getText().toString());
               desc.setText(desc.getText().toString());
               contact.setText(contact.getText().toString());
               link.setText(link.getText().toString());

           }

       });

    }
}

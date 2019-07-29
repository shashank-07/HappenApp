package com.example.shash.shashanksummer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.shash.shashanksummer.Message;
import com.example.shash.shashanksummer.R;

import java.util.ArrayList;
public class MessageAdapter extends ArrayAdapter<Message> {
    Context mContext;
    public MessageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        mContext=context;

    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String mag=getItem(position).getText();
        String mag2=getItem(position).getDate();
        LayoutInflater inflater= LayoutInflater.from(mContext);
        convertView=inflater.inflate(R.layout.item_message,parent,false);
        final TextView tvMag=(TextView) convertView.findViewById(R.id.messageTextView);
        final TextView lol=(TextView) convertView.findViewById(R.id.nameTextView);
        tvMag.setText(mag);
        lol.setText(mag2);
        return convertView;



    }
}

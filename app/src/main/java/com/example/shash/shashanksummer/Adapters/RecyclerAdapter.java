package com.example.shash.shashanksummer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shash.shashanksummer.Message;
import com.example.shash.shashanksummer.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Message> messages;
    private Context mContext;

    public RecyclerAdapter(ArrayList<Message> messages, Context mContext) {
        this.messages = messages;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.latest.setText(messages.get(position).getText());
        holder.sender.setText(messages.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView latest;
        TextView sender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            latest=itemView.findViewById(R.id.messageTextView);
            sender=itemView.findViewById(R.id.nameTextView);

        }
    }
}

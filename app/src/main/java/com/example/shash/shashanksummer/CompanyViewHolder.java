package com.example.shash.shashanksummer;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class CompanyViewHolder extends GroupViewHolder {
    private TextView mTextView;
    ImageView arrow;
    private ImageView img;
    public CompanyViewHolder(View itemView) {
        super(itemView);
        mTextView=itemView.findViewById(R.id.textView);
        arrow=itemView.findViewById(R.id.arrow);
        img=itemView.findViewById(R.id.typeImage);


    }
    public void bind(Company company){
        mTextView.setText(company.getTitle());
        String eventselec=company.catego;
        if(eventselec.matches("Party")){
            img.setImageResource(R.drawable.snap);


        }
        if(eventselec.matches("Group Study")){
            img.setImageResource(R.drawable.study);
        }if(eventselec.matches("Club meeting")){
            img.setImageResource(R.drawable.clube);

        }if(eventselec.matches("Guest lecture")){
           img.setImageResource(R.drawable.gle);


        }if(eventselec.matches("Fest event")){
            img.setImageResource(R.drawable.feste);

        }if(eventselec.matches("Other")){
            img.setImageResource(R.drawable.othere);


        }

    }
    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }
}

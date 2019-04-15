package com.azwraithnp.eadnepal.main.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.History;
import com.azwraithnp.eadnepal.main.Models.Interest;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.MyViewHolder>{

    private final ArrayList<Interest> dataSet;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        RelativeLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image2);
            layout = itemView.findViewById(R.id.card_view2);

    }

    }


    public InterestsAdapter(Context context, ArrayList<Interest> dataSet)
    {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_interest, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        Glide.with(context).load("http://eadnepal.com/management/pages/Add%20Interest/uploads/" + dataSet.get(listPosition).getImage()).into(holder.imageView);

        holder.itemView.setTag(dataSet.get(listPosition).getId());


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}

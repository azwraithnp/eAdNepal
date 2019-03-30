package com.azwraithnp.eadnepal.main.Adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.History;

import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;

public class VerifyAdapter extends RecyclerView.Adapter<VerifyAdapter.MyViewHolder>{

    private final ArrayList<History> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView snText, nameText, phoneText;

        ArrayList<History> dataSet = new ArrayList<>();

        public MyViewHolder(View itemView) {
            super(itemView);
            snText = itemView.findViewById(R.id.SNtext);
            nameText = itemView.findViewById(R.id.nameText);
            phoneText = itemView.findViewById(R.id.phoneText);
        }
    }

    public VerifyAdapter(ArrayList<History> dataSet)
    {
        this.dataSet = dataSet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verify, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView snText = holder.snText;
        TextView nameText = holder.nameText;
        TextView dateText = holder.phoneText;

        if(dataSet.get(listPosition).getSn().equals("S.N"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snText.setTextAppearance(BOLD);
                nameText.setTextAppearance(BOLD);
                dateText.setTextAppearance(BOLD);
            }
        }

        snText.setText(dataSet.get(listPosition).getSn());
        nameText.setText(dataSet.get(listPosition).getAdName());
        dateText.setText(dataSet.get(listPosition).getViewDate());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

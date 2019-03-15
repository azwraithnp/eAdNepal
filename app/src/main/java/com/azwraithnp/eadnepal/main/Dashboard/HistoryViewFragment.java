package com.azwraithnp.eadnepal.main.Dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Adapters.HistoryAdapter;
import com.azwraithnp.eadnepal.main.Models.History;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryViewFragment extends Fragment {

    RecyclerView recyclerView;


    public HistoryViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_history_view, container, false);

        HistoryAdapter historyAdapter = new HistoryAdapter(convertGson(getArguments().getStringArrayList("history_list")));

        recyclerView = v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(historyAdapter);

        return v;
    }


    public ArrayList<History> convertGson(ArrayList<String> tempArray)
    {
        ArrayList<History> temp = new ArrayList<>();

        for(String json: tempArray)
        {
            temp.add(new Gson().fromJson(json, History.class));
        }

        return temp;
    }

}

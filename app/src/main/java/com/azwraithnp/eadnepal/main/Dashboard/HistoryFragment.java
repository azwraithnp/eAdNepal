package com.azwraithnp.eadnepal.main.Dashboard;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.History;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    CardView pictureButton, videoButton, audioButton;
    ArrayList<History> histories;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getActivity() != null)
        {
            ((Dashboard)getActivity()).changeText("History");
            ((Dashboard)getActivity()).currentFragment="HistoryFragment";
        }

        final UserModel user = new Gson().fromJson(getArguments().getString("User"), UserModel.class);

        View v = inflater.inflate(R.layout.fragment_history, container, false);

        setupViews(v);

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveHistory(user, AppConfig.URL_IMAGES_HISTORY);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveHistory(user, AppConfig.URL_VIDEO_HISTORY);
            }
        });

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveHistory(user, AppConfig.URL_AUDIO_HISTORY);
            }
        });

        return v;
    }

    private void setupViews(View v) {

        pictureButton = v.findViewById(R.id.pictureHistoryCard);
        videoButton = v.findViewById(R.id.videoHistoryCard);
        audioButton = v.findViewById(R.id.audioHistoryCard);
        histories = new ArrayList<>();

    }

    private ArrayList<String> convertGson(ArrayList<History> abc)
    {
        ArrayList<String> temp = new ArrayList<>();

        for(History history: abc)
        {
            temp.add(new Gson().toJson(history));
        }

        return temp;
    }


    public void retrieveHistory(final UserModel user, final String history_url)
    {
        String tag_string_req = "req_history";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                history_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("History", "History Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    histories.add(new History("S.N", "Ad Name", "View Date"));

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        String date = dataObj.getString("added_date");

                        History history  = new History((i+1) + "", name, date);
                        histories.add(history);
                    }

                    ArrayList<String> historyList = convertGson(histories);

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("history_list", historyList);
                    bundle.putString("history_type", history_url);

                    HistoryViewFragment historyViewFragment = new HistoryViewFragment();
                    historyViewFragment.setArguments(bundle);
                    if(getActivity() != null)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, historyViewFragment).addToBackStack("historyMain").commit();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("History", "History Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("ead_token", AppConfig.EAD_TOKEN);
                params.put("uid", user.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


}

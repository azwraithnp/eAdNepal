package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Adapters.HistoryAdapter;
import com.azwraithnp.eadnepal.main.Models.History;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.azwraithnp.eadnepal.main.helper_classes.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<History> histories = new ArrayList<>();
    HistoryAdapter historyAdapter;
    AVLoadingIndicatorView avLoadingIndicatorView;
    RelativeLayout mainLoadingView;

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_verify, container, false);

        if(getActivity() !=null)
        {
            ((Dashboard)getActivity()).changeText("Verify Client");
            ((Dashboard)getActivity()).currentFragment="VerifyFragment";
        }

        final UserModel user = new Gson().fromJson(getArguments().getString("User"), UserModel.class);
        historyAdapter = new HistoryAdapter(histories);

        recyclerView = v.findViewById(R.id.verify_recycler_view);
        recyclerView.setHasFixedSize(true);

        avLoadingIndicatorView = v.findViewById(R.id.avi);
        mainLoadingView = v.findViewById(R.id.loadingView);
        mainLoadingView.setVisibility(View.GONE);

        retrieveVerifyList(user);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(historyAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {

                        ClientDetailsFragment clientDetailsFragment = new ClientDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("admin_email", user.getEmail());
                        bundle.putString("client_phone", histories.get(position).getViewDate());
                        clientDetailsFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, clientDetailsFragment).addToBackStack("client").commit();


                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        return v;
    }


    public void retrieveVerifyList(final UserModel user)
    {
        String tag_string_req = "req_verify";
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Verify", "Verify Response: " + response.toString());

                try {

                    histories.clear();

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    histories.add(new History("S.N", "Client Name", "Client Contact"));

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String fname = dataObj.getString("f_name");
                        String lname = dataObj.getString("l_name");
                        String phone = dataObj.getString("phone");

                        History history  = new History((i+1) + "", fname+lname, phone);
                        histories.add(history);
                    }

                    historyAdapter.notifyDataSetChanged();

                    hideDialog();



                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Verify", "Verify Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email)
                params.put("admin_token", user.getEmail());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void hideDialog() {
//        if (progressDialog.isShowing())
//            progressDialog.dismiss();
        mainLoadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).showToolbar();
    }

    private void showDialog() {
//        if (!progressDialog.isShowing())
//            progressDialog.show();
        mainLoadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).hideToolbar();
    }

}

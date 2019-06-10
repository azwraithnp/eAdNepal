package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Adapters.CardAdapter;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.azwraithnp.eadnepal.main.helper_classes.GridSpacingItemDecoration;
import com.azwraithnp.eadnepal.main.helper_classes.RecyclerItemClickListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Album> videoList;

    private CardAdapter cardAdapter;

    private ProgressBar progressBar;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        assert getArguments() != null;

        Gson gson = new Gson();

        videoList = new ArrayList<>();

        final UserModel user = gson.fromJson(getArguments().getString("User"), UserModel.class);

        if(getActivity()!=null)
        {
            ((Dashboard)getActivity()).changeText("All Video");
            ((Dashboard)getActivity()).currentFragment = "VideoFragment";
        }

        View v = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = v.findViewById(R.id.all_video_recycler_view);

        cardAdapter = new CardAdapter(getActivity(), videoList, "videoall");

        progressBar = v.findViewById(R.id.loadingProgressBar);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && videoList.size() <=1) {
                    retrieveVideos(user, videoList.get(videoList.size()-1).getId());
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {
                        if(videoList.get(position).getId().equals("8888"))
                        {

                        }
                        else {


                            Bundle bundle = new Bundle();
                            bundle.putString("video", new Gson().toJson(videoList.get(position)));
                            bundle.putString("userId", user.getId());
                            bundle.putString("contentType", "video");

                            VideoViewFragment videoViewFragment = new VideoViewFragment();
                            videoViewFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoViewFragment).addToBackStack("homeFragment").commit();

                            String holderName = "";
                            if(videoList.get(position).getName().length() > 15)
                            {
                                for(int i=0;i<10;i++)
                                {
                                    holderName+= videoList.get(position).getName().charAt(i);
                                }
                                holderName+="...";
                            }
                            else
                            {
                                holderName = videoList.get(position).getName();
                            }

                            ((Dashboard)getActivity()).changeText(holderName);


//                            ViewGroup.LayoutParams params=videoview.getLayoutParams();
//                            params.height= 500;
//                            videoview.setLayoutParams(params);
                        }
                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        retrieveVideos(user, "0");

        return v;

    }

    public void transferBalance(final String mediaId, String url, final UserModel user)
    {
        String tag_string_req = "req_transfer";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Transfer", "Transfer Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    String status = jObj.getString("status");
                    String status_message = jObj.getString("status_message");
                    String data = jObj.getString("data");

                    Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Transfer", "Transfer Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("npl_token", "a");
                params.put("npl_aid", mediaId);
                params.put("npl_id", user.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void retrieveVideos(final UserModel user, final String aid)
    {
        progressBar.setVisibility(View.VISIBLE);

        String tag_string_req = "req_videos";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALL_VIDEO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("Video", "Video Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int payOut = Integer.parseInt(dataObj.getString("reach_out_price"));
                        String video = dataObj.getString("video");
                        String des = dataObj.getString("des");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String url = dataObj.getString("url");

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, des, payOut, video, email, phone, url);
                        videoList.add(album);
                    }

                    cardAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // JSON error
                    videoList.add(new Album("8888", "No more ads", "no ads", 0, "abc", "na", "na", "na"));
                    cardAdapter.notifyDataSetChanged();
                    Log.d("Video", "Video error:" + e.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Video", "Video Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("ead_token", AppConfig.EAD_TOKEN);
                params.put("location", user.getLocation());
                params.put("age", user.getAge());
                params.put("sex", user.getSex());
                params.put("uid", user.getId());
                params.put("aid", aid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}

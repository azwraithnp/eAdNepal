package com.azwraithnp.eadnepal.main.Dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.circularreveal.CircularRevealLinearLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Adapters.CardAdapter;
import com.azwraithnp.eadnepal.main.Adapters.SlidingImage_Adapter;
import com.azwraithnp.eadnepal.main.Login.MainActivity;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.azwraithnp.eadnepal.main.helper_classes.GridSpacingItemDecoration;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private  ViewPager mPager;
    private CirclePageIndicator indicator;
    private  int currentPage = 0;
    private  int NUM_PAGES = 0;

    private RecyclerView photoRecyclerView;
    private CardAdapter photoAdapter;

    private RecyclerView videoRecyclerView;
    private CardAdapter videoAdapter;

    private RecyclerView audioRecyclerView;
    private CardAdapter audioAdapter;

    SlidingImage_Adapter slidingImage_adapter;

    private List<Album> photoList;
    private List<Album> videoList;
    private List<Album> audioList;
    private ArrayList<String> imageList;

    private String userJSON;

    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        assert getArguments() != null;
        this.userJSON = getArguments().getString("User");

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Gson gson = new Gson();
        UserModel user = gson.fromJson(userJSON, UserModel.class);

        retrieveImages(user);

        retrieveAudio(user);

        retrieveVideos(user);

        populateList();

        setupViews(v);

        return v;
    }

    public void setupViews(View v)
    {
        mPager = (ViewPager) v.findViewById(R.id.pager);
        indicator = (CirclePageIndicator)
                v.findViewById(R.id.indicator);


        photoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        photoRecyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        photoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        photoRecyclerView.setAdapter(photoAdapter);

        videoRecyclerView = (RecyclerView) v.findViewById(R.id.video_recycler_view);
        RecyclerView.LayoutManager nLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(nLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        videoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoRecyclerView.setAdapter(videoAdapter);

        audioRecyclerView = (RecyclerView) v.findViewById(R.id.audio_recycler_view);
        RecyclerView.LayoutManager oLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        audioRecyclerView.setLayoutManager(oLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        audioRecyclerView.setItemAnimator(new DefaultItemAnimator());
        audioRecyclerView.setAdapter(audioAdapter);


    }

    public void retrieveImages(final UserModel user)
    {
        String tag_string_req = "req_images";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_IMAGES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Image", "Image Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int timeCount = 15;
                        String image = dataObj.getString("doc_image");

                        Album album = new Album(name, timeCount, image);
                        photoList.add(album);

                    }

                    init();

                    photoList.add(new Album("View more", 0, "abc"));

                    photoAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Image", "Image Error: " + error.getMessage());

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void retrieveVideos(final UserModel user)
    {
        String tag_string_req = "req_videos";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VIDEOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Video", "Video Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int timeCount = 15;
                        String video = dataObj.getString("video");

                        Album album = new Album(name, timeCount, video);
                        videoList.add(album);
                    }

                    videoList.add(new Album("View more", 0, "abc"));

                    videoAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void retrieveAudio(final UserModel user)
    {
        String tag_string_req = "req_audio";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_AUDIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Audio", "Audio Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int timeCount = 15;
                        String audio = dataObj.getString("audio");

                        Album album = new Album(name, timeCount, audio);
                        audioList.add(album);
                    }

                    audioList.add(new Album("View more", 0, "abc"));

                    audioAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Audio", "Audio Error: " + error.getMessage());

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void populateList()
    {
        photoList = new ArrayList<>();

        photoAdapter = new CardAdapter(getActivity(), photoList, "photo");

        videoList = new ArrayList<>();

        videoAdapter = new CardAdapter(getActivity(), videoList, "video");

        audioList = new ArrayList<>();

        audioAdapter = new CardAdapter(getActivity(), audioList, "audio");

        imageList = new ArrayList<>();

        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), imageList);

    }


    public void init()
    {

        Log.d("Count", "Here");

        List<Album> newAlbum;
        newAlbum = new ArrayList<>(photoList);

        for(Album testAlbum: newAlbum)
        {
            imageList.add(testAlbum.getThumbnail());
        }

        slidingImage_adapter.notifyDataSetChanged();

        mPager.setAdapter(slidingImage_adapter);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });



    }


}

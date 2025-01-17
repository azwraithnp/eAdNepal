package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.circularreveal.CircularRevealLinearLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.azwraithnp.eadnepal.main.helper_classes.RecyclerItemClickListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private  ViewPager mPager;
    private CirclePageIndicator indicator;
    private  int currentPage = 0;
    private  int NUM_PAGES = 0;
    public int MAX_HEIGHT = 0;

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
    private ArrayList<Album> imageList;
    private ArrayList<String> albumTypes;

    private String userJSON;

    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    private CountDownTimer countDownTimer;
    private Timer viewPagerTimer;

    private SharedPreferences sharedPreferences;

    private UserModel userObj;

    AVLoadingIndicatorView avLoadingIndicatorView;

    ScrollView mainScrollView;
    RelativeLayout mainLoadingView;

    boolean stopped = false;

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

        if(getActivity() !=null)
        {
            ((Dashboard)getActivity()).changeText(getResources().getString(R.string.app_name));
            ((Dashboard)getActivity()).currentFragment="HomeFragment";
        }

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);


        avLoadingIndicatorView = v.findViewById(R.id.avi);
        mainLoadingView = v.findViewById(R.id.loadingView);
        mainLoadingView.setVisibility(View.GONE);

        mainScrollView = v.findViewById(R.id.mainScrollView);

        Gson gson = new Gson();
        UserModel user = gson.fromJson(userJSON, UserModel.class);

        userObj = user;

        sharedPreferences = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

        retrieveImages(user);

        retrieveAudio(user);

        retrieveVideos(user);

        retrieveFeatured(user);

        populateList();

        setupViews(v);

        checkLogin(sharedPreferences.getString("userEmail", ""), sharedPreferences.getString("userPassword", ""));

        return v;
    }

    public void setupViews(View v)
    {
        mPager = (ViewPager) v.findViewById(R.id.pager);
        indicator = (CirclePageIndicator)
                v.findViewById(R.id.indicator);

        photoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        photoRecyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        photoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        photoRecyclerView.setAdapter(photoAdapter);
        photoRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), photoRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, final int position)
            {
                if(photoList.get(position).getId().equals("8888"))
                {

                }
                else
                {
                    if(position == photoAdapter.getItemCount() - 1)
                    {
                        PictureFragment pictureFragment = new PictureFragment();
                        pictureFragment.setArguments(getArguments());
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, pictureFragment).addToBackStack("allpic").commit();
                        ((Dashboard)getActivity()).changeText("All Picture");
                    }
                    else
                    {
                        Log.d("sizeArray", photoList.size() + " : " + position);

                        Bundle bundle = new Bundle();
                        bundle.putString("picture", new Gson().toJson(photoList.get(position)));
                        bundle.putString("userId", userObj.getId());
                        bundle.putString("contentType", "picture");

                        PictureViewFragment pictureViewFragment = new PictureViewFragment();
                        pictureViewFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, pictureViewFragment).addToBackStack("homeFragment").commit();

                        String holderName = "";
                        if(photoList.get(position).getName().length() > 15)
                        {
                            for(int i=0;i<10;i++)
                            {
                                holderName+= photoList.get(position).getName().charAt(i);
                            }
                            holderName+="...";
                        }
                        else
                        {
                            holderName = photoList.get(position).getName();
                        }

                        ((Dashboard)getActivity()).changeText(holderName);

                    }
                }
            }
            @Override public void onLongItemClick(View view, int position)
            {

            }
            })
        );

        videoRecyclerView = (RecyclerView) v.findViewById(R.id.video_recycler_view);
        RecyclerView.LayoutManager nLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(nLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        videoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), videoRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {
                        if(videoList.get(position).getId().equals("8888"))
                        {

                        }
                        else
                        {
                            if(position == videoAdapter.getItemCount() - 1)
                            {
                                VideoFragment videoFragment = new VideoFragment();
                                videoFragment.setArguments(getArguments());
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoFragment).addToBackStack("allvideo").commit();
                                ((Dashboard)getActivity()).changeText("All Video");
                            }
                            else
                            {
                                Bundle bundle = new Bundle();
                                bundle.putString("video", new Gson().toJson(videoList.get(position)));
                                bundle.putString("userId", userObj.getId());
                                bundle.putString("User", getArguments().getString("User"));
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
                            }
                        }
                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        audioRecyclerView = (RecyclerView) v.findViewById(R.id.audio_recycler_view);
        RecyclerView.LayoutManager oLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        audioRecyclerView.setLayoutManager(oLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        audioRecyclerView.setItemAnimator(new DefaultItemAnimator());
        audioRecyclerView.setAdapter(audioAdapter);
        audioRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), audioRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {

                        stopped = false;

                        Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                        if(audioList.get(position).getId().equals("8888"))
                        {

                        }
                        else{
                            if(position == audioAdapter.getItemCount() - 1) {
                                AudioFragment audioFragment = new AudioFragment();
                                audioFragment.setArguments(getArguments());
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, audioFragment).addToBackStack("allaudio").commit();
                                ((Dashboard)getActivity()).changeText("All Audio");
                            }
                            else
                            {
                                Bundle bundle = new Bundle();
                                bundle.putString("video", new Gson().toJson(audioList.get(position)));
                                bundle.putString("userId", userObj.getId());
                                bundle.putString("contentType", "audio");

                                VideoViewFragment videoViewFragment = new VideoViewFragment();
                                videoViewFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoViewFragment).addToBackStack("audioFragment").commit();

                                String holderName = "";
                                if(audioList.get(position).getName().length() > 15)
                                {
                                    for(int i=0;i<10;i++)
                                    {
                                        holderName+= audioList.get(position).getName().charAt(i);
                                    }
                                    holderName+="...";
                                }
                                else
                                {
                                    holderName = audioList.get(position).getName();
                                }

                                ((Dashboard)getActivity()).changeText(holderName);
                            }
                        }

                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );


    }

    public void viewPagerPicture()
    {
        Bundle bundle = new Bundle();
        bundle.putString("picture", new Gson().toJson(imageList.get(0)));
        bundle.putString("userId", userObj.getId());
        bundle.putString("contentType", "picture");

        PictureViewFragment pictureViewFragment = new PictureViewFragment();
        pictureViewFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, pictureViewFragment).addToBackStack("pictureFragment").commit();

        String holderName = "";
        if(imageList.get(0).getName().length() > 15)
        {
            for(int i=0;i<10;i++)
            {
                holderName+= imageList.get(0).getName().charAt(i);
            }
            holderName+="...";
        }
        else
        {
            holderName = imageList.get(0).getName();
        }

        ((Dashboard)getActivity()).changeText(holderName);

    }

    public void viewPagerVideo()
    {
        int position = 0;

        for(int i=0;i<albumTypes.size();i++)
        {
            if(albumTypes.get(i).equals("video"))
                position = i;
        }

        Bundle bundle = new Bundle();
        bundle.putString("video", new Gson().toJson(imageList.get(position)));
        bundle.putString("userId", userObj.getId());
        bundle.putString("User", getArguments().getString("User"));
        bundle.putString("contentType", "video");

        VideoViewFragment videoViewFragment = new VideoViewFragment();
        videoViewFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoViewFragment).addToBackStack("homeFragment").commit();

        String holderName = "";
        if(imageList.get(position).getName().length() > 15)
        {
            for(int i=0;i<10;i++)
            {
                holderName+= imageList.get(position).getName().charAt(i);
            }
            holderName+="...";
        }
        else
        {
            holderName = imageList.get(position).getName();
        }

        ((Dashboard)getActivity()).changeText(holderName);
//                            ViewGroup.LayoutParams params=videoview.getLayoutParams();
//                            params.height= 500;
//                            videoview.setLayoutParams(params);
    }

    public void viewPagerAudio()
    {

        int position = 0;

        for(int i=0;i<albumTypes.size();i++)
        {
            if(albumTypes.get(i).equals("audio"))
                position = i;
        }

        Bundle bundle = new Bundle();
        bundle.putString("video", new Gson().toJson(audioList.get(position)));
        bundle.putString("userId", userObj.getId());
        bundle.putString("contentType", "audio");

        VideoViewFragment videoViewFragment = new VideoViewFragment();
        videoViewFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoViewFragment).addToBackStack("audioFragment").commit();

        String holderName = "";
        if(audioList.get(position).getName().length() > 15)
        {
            for(int i=0;i<10;i++)
            {
                holderName+= audioList.get(position).getName().charAt(i);
            }
            holderName+="...";
        }
        else
        {
            holderName = audioList.get(position).getName();
        }

        ((Dashboard)getActivity()).changeText(holderName);
    }

    public void retrieveImages(final UserModel user)
    {
        showDialog();


        String tag_string_req = "req_images";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_IMAGES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Image", "Image Response: " + response.toString());

               hideDialog();

                photoList.clear();

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        String image = dataObj.getString("doc_image");
                        String payout = dataObj.getString("reach_out_price");
                        String desc = dataObj.getString("des");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String url = dataObj.getString("url");

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, desc, Integer.parseInt(payout), image, email, phone, url);
                        photoList.add(album);

                    }

                    photoList.add(new Album("999","View more", "view more", 0, "abc", "vm", "vm", "vm"));

                    photoAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // JSON error
                    photoList.add(new Album("8888", "No more ads", "no ads", 0, "abc", "na", "na", "na"));
                    photoAdapter.notifyDataSetChanged();
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
                params.put("uid", user.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }



    public void retrieveVideos(final UserModel user)
    {
        String tag_string_req = "req_videos";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VIDEOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Video", "Video Response: " + response.toString());

                hideDialog();

                videoList.clear();

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int payout = Integer.parseInt(dataObj.getString("reach_out_price"));
                        String video = dataObj.getString("video");
                        String desc = dataObj.getString("des");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String url = dataObj.getString("url");

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, desc, payout, video, email, phone, url);
                        videoList.add(album);
                    }

                    videoList.add(new Album("9999", "View more", "view more", 0, "abc", "na", "na", "na"));

                    videoAdapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    videoList.add(new Album("8888", "No more ads", "no ads", 0, "abc", "na", "na", "na"));
                    videoAdapter.notifyDataSetChanged();
                    // JSON error
                    Log.d("Video", "Video error: " + e.toString());
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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void retrieveAudio(final UserModel user)
    {
        String tag_string_req = "req_audio";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_AUDIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Audio", "Audio Response: " + response.toString());

                hideDialog();

                audioList.clear();

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int payout = Integer.parseInt(dataObj.getString("reach_out_price"));
                        String audio = dataObj.getString("audio");
                        String desc = dataObj.getString("des");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String url = dataObj.getString("url");

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, desc, payout, audio, email, phone, url);
                        audioList.add(album);
                    }

                    audioList.add(new Album("999", "View more", "view more", 0, "abc","na","na","na"));

                    audioAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    // JSON error
                    audioList.add(new Album("8888", "No more ads", "no ads", 0, "abc","na","na","na"));
                    audioAdapter.notifyDataSetChanged();
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
                params.put("uid", user.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    @Override
    public void onPause() {
        super.onPause();
        if(viewPagerTimer != null)
            viewPagerTimer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(viewPagerTimer != null)
            viewPagerTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(viewPagerTimer != null)
            viewPagerTimer.cancel();
    }

    public void transferBalance(final String mediaId, final String url, final UserModel user)
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

                    if(url.equals(AppConfig.URL_TRANSFER_PICTURE))
                    {
                        retrieveImages(user);
                    }
                    else if(url.equals(AppConfig.URL_TRANSFER_AUDIO))
                    {
                        retrieveAudio(user);
                    }
                    else
                    {
                        retrieveVideos(user);
                    }

                    retrieveFeatured(user);

                    checkLogin(sharedPreferences.getString("userEmail", ""), sharedPreferences.getString("userPassword", ""));

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


    public void populateList()
    {
        photoList = new ArrayList<>();

        photoAdapter = new CardAdapter(getActivity(), photoList, "photo");

        videoList = new ArrayList<>();

        videoAdapter = new CardAdapter(getActivity(), videoList, "video");

        audioList = new ArrayList<>();

        audioAdapter = new CardAdapter(getActivity(), audioList, "audio");

        imageList = new ArrayList<>();

        albumTypes = new ArrayList<>();

        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), imageList, albumTypes,this);

    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Login", "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    JSONObject dataObj = jsonArray.getJSONObject(0);

                    UserModel user = new UserModel( getString(dataObj, "f_name"),
                            getString(dataObj, "l_name"),
                            getString(dataObj, "email"),
                            getString(dataObj, "phone"),
                            getString(dataObj, "location"),
                            getString(dataObj, "college"),
                            getString(dataObj, "level"),
                            getString(dataObj, "field_of_study"),
                            getString(dataObj, "company"),
                            getString(dataObj, "post"),
                            getString(dataObj, "interest"),
                            getString(dataObj, "id"),
                            getString(dataObj, "reg_date"),
                            getString(dataObj, "balance"),
                            getString(dataObj, "age"),
                            getString(dataObj, "sex"));

                    Log.d("User", user.toString());

                    userObj = user;

                    if(getActivity() !=null)
                        ((Dashboard)getActivity()).changeNameBalance(user.getF_name() + " " + user.getL_name(), user.getBalance());


                } catch (JSONException e) {
                    // JSON error
                    Log.d("Video", "Video error: " + e.toString());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideDialog();
                Log.e("Login", "Login Error: " + error.getMessage());


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("ead_token", AppConfig.EAD_TOKEN);
                params.put("ead_email", email);
                params.put("ead_password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String getString(JSONObject jsonObject, String stringToken) throws JSONException {
        return jsonObject.getString(stringToken);
    }



    public void init()
    {

        Log.d("Count", "Here" + imageList.get(0));

        slidingImage_adapter.notifyDataSetChanged();

        mPager.setAdapter(slidingImage_adapter);

        indicator.setViewPager(mPager);

        float density = 0;

        if(getActivity() !=null)
        {
            density = getResources().getDisplayMetrics().density;
        }

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
        viewPagerTimer = new Timer();
        viewPagerTimer.schedule(new TimerTask() {
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

    public void retrieveFeatured(final UserModel user)
    {
        String tag_string_req = "req_featured";
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FEATURED, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Featured", "Featured Response: " + response.toString());

                try {
                    imageList.clear();
                    albumTypes.clear();

                    hideDialog();

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++) {

                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        Log.d("abcd", dataObj.toString());
                        Log.d("abcd", i + "");

                        int timeCount = 15;

                        String id = dataObj.getString("id");
                        String name = dataObj.getString("a_title");
                        String desc = dataObj.getString("des");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String url = dataObj.getString("url");
                        String thumbnail = "";
                        String albumType = "";

                        try {
                            thumbnail = dataObj.getString("doc_image");
                            albumType = "photo";
                        } catch (JSONException e) {
                            try {
                                thumbnail = dataObj.getString("audio");
                                albumType = "audio";
                            } catch (JSONException exc) {
                                thumbnail = dataObj.getString("video");
                                albumType = "video";
                            }
                        }

                        Album album = new Album(id, name, desc, timeCount, thumbnail,email, phone, url);
                        imageList.add(album);
                        albumTypes.add(albumType);

                        init();
                    }


                } catch (JSONException e) {
                    // JSON error

                    Log.d("abcd", "error");

                    Album album = new Album("8888", "No more ads", "no ads", 0, "abc", "na","na","na");
                    imageList.add(album);
                    init();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Featured", "Featured Error: " + error.getMessage());

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    private void showDialog() {
        mainLoadingView.setVisibility(View.VISIBLE);
        mainScrollView.setVisibility(View.GONE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).hideToolbar();
    }

    public void setMAX_HEIGHT(int MAX_HEIGHT)
    {
        this.MAX_HEIGHT = MAX_HEIGHT;
    }

    private void hideDialog() {
        mainLoadingView.setVisibility(View.GONE);
        mainScrollView.setVisibility(View.VISIBLE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).showToolbar();
    }


}

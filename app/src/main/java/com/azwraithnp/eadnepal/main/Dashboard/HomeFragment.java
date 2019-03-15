package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private ArrayList<String> idsList;

    private String userJSON;

    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;

    private UserModel userObj;

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

        userObj = user;

        retrieveImages(user);

        retrieveAudio(user);

        retrieveVideos(user);

        retrieveFeatured(user);

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
        photoRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), photoRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position)
            {
                if(position == photoAdapter.getItemCount() - 1)
                {
                    PictureFragment pictureFragment = new PictureFragment();
                    pictureFragment.setArguments(getArguments());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, pictureFragment).commit();
                }
                else
                {

                    String url = "http://eadnepal.com/client/pages/target/uploads/" + photoList.get(position).getThumbnail();

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.intropicture);
                    dialog.show();

                    final String id = photoList.get(position).getId();

                    ImageView img = dialog.findViewById(R.id.adPic);
                    Glide.with(getActivity()).load(url).into(img);

                    progressBar=(ProgressBar)dialog.findViewById(R.id.progressbar);
                    progressBar.setProgress(0);
                    countDownTimer=new CountDownTimer(15000,1000) {

                        int i=0;
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                            i++;
                            progressBar.setProgress((int)i*100/(15000/1000));
                        }

                        @Override
                        public void onFinish() {
                            //Do what you want
                            i++;
                            progressBar.setProgress(100);
                            dialog.cancel();
                            Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                            transferBalance(id, AppConfig.URL_TRANSFER_PICTURE, userObj);
                        }
                    };
                    countDownTimer.start();

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                    });

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
                        if(position == videoAdapter.getItemCount() - 1)
                        {
                            VideoFragment videoFragment = new VideoFragment();
                            videoFragment.setArguments(getArguments());
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoFragment).commit();
                        }
                        else
                        {

                            String url = "http://eadnepal.com/client/pages/target video/uploads/" + videoList.get(position).getThumbnail();

                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.introvid);
                            dialog.show();
                            Toast.makeText(getActivity(), "Loading..", Toast.LENGTH_SHORT).show();
                            final VideoView videoview = (VideoView) dialog.findViewById(R.id.videoView);
                            videoview.setVideoPath(url);
                            videoview.start();

                            final ProgressDialog prog = ProgressDialog.show(getActivity(), "Please wait ...", "Retrieving data ...", true, false);

                            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    prog.dismiss();
                                }
                            });

                            videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    dialog.cancel();
                                    Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                                    transferBalance(videoList.get(position).getId(), AppConfig.URL_TRANSFER_VIDEO, userObj);
                                }
                            });
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

        audioRecyclerView = (RecyclerView) v.findViewById(R.id.audio_recycler_view);
        RecyclerView.LayoutManager oLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        audioRecyclerView.setLayoutManager(oLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(albumList.size(), dpToPx(10), true));
        audioRecyclerView.setItemAnimator(new DefaultItemAnimator());
        audioRecyclerView.setAdapter(audioAdapter);
        audioRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), audioRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {
                        if(position == audioAdapter.getItemCount() - 1) {
                            AudioFragment audioFragment = new AudioFragment();
                            audioFragment.setArguments(getArguments());
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, audioFragment).commit();
                        }
                        else
                        {
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.intromusic);
                            dialog.show();

                            Toast.makeText(getActivity(), "Now Playing..", Toast.LENGTH_SHORT).show();

                            String url = "http://eadnepal.com/client/pages/target%20audio/uploads/" + audioList.get(position).getThumbnail(); // your URL here
                            final MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try
                            {
                                mediaPlayer.setDataSource(url);
                                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            mediaPlayer.start();

                            final ProgressDialog prog = ProgressDialog.show(getActivity(), "Please wait ...", "Retrieving data ...", true, false);

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    prog.dismiss();
                                }
                            });

                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }
                            });

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mediaPlayer.release();
                                    dialog.cancel();
                                    Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                                    transferBalance(audioList.get(position).getId(), AppConfig.URL_TRANSFER_AUDIO, userObj);
                                }
                            });
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
        String url = "http://eadnepal.com/client/pages/target/uploads/" + imageList.get(0);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.intropicture);
        dialog.show();

        final String id = idsList.get(0);

        ImageView img = dialog.findViewById(R.id.adPic);
        Glide.with(getActivity()).load(url).into(img);

        progressBar=(ProgressBar)dialog.findViewById(R.id.progressbar);
        progressBar.setProgress(0);
        countDownTimer=new CountDownTimer(15000,1000) {

            int i=0;
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                progressBar.setProgress((int)i*100/(15000/1000));
            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                progressBar.setProgress(100);
                dialog.cancel();
                Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                transferBalance(id, AppConfig.URL_TRANSFER_PICTURE, userObj);
            }
        };
        countDownTimer.start();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        });

    }

    public void viewPagerVideo()
    {
        String url = "http://eadnepal.com/client/pages/target video/uploads/" + imageList.get(2);

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.introvid);
        dialog.show();
        Toast.makeText(getActivity(), "Loading..", Toast.LENGTH_SHORT).show();
        final VideoView videoview = (VideoView) dialog.findViewById(R.id.videoView);
        videoview.setVideoPath(url);
        videoview.start();

        final ProgressDialog prog = ProgressDialog.show(getActivity(), "Please wait ...", "Retrieving data ...", true, false);

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prog.dismiss();
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialog.cancel();
                Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                transferBalance(idsList.get(2), AppConfig.URL_TRANSFER_VIDEO, userObj);
            }
        });
//                            ViewGroup.LayoutParams params=videoview.getLayoutParams();
//                            params.height= 500;
//                            videoview.setLayoutParams(params);
    }

    public void viewPagerAudio()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.intromusic);
        dialog.show();

        Toast.makeText(getActivity(), "Now Playing..", Toast.LENGTH_SHORT).show();

        String url = "http://eadnepal.com/client/pages/target%20audio/uploads/" + imageList.get(1); // your URL here
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();

        final ProgressDialog prog = ProgressDialog.show(getActivity(), "Please wait ...", "Retrieving data ...", true, false);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prog.dismiss();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                dialog.cancel();
                Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                transferBalance(idsList.get(1), AppConfig.URL_TRANSFER_AUDIO, userObj);
            }
        });
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

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, timeCount, image);
                        photoList.add(album);

                    }

                    photoList.add(new Album("999","View more", 0, "abc"));

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

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, timeCount, video);
                        videoList.add(album);
                    }

                    videoList.add(new Album("9999", "View more", 0, "abc"));

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

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, timeCount, audio);
                        audioList.add(album);
                    }

                    audioList.add(new Album("999", "View more", 0, "abc"));

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
                params.put("uid", user.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


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


    public void populateList()
    {
        photoList = new ArrayList<>();

        photoAdapter = new CardAdapter(getActivity(), photoList, "photo");

        videoList = new ArrayList<>();

        videoAdapter = new CardAdapter(getActivity(), videoList, "video");

        audioList = new ArrayList<>();

        audioAdapter = new CardAdapter(getActivity(), audioList, "audio");

        imageList = new ArrayList<>();

        idsList = new ArrayList<>();

        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), imageList, this);

    }


    public void init()
    {

        Log.d("Count", "Here");

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

    public void retrieveFeatured(final UserModel user)
    {
        String tag_string_req = "req_featured";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FEATURED, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Featured", "Featured Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        int timeCount = 15;

                        switch (i)
                        {
                            case 0:
                                String id = dataObj.getString("id");
                                String name = dataObj.getString("a_title");
                                String thumbnail = dataObj.getString("doc_image");

                                Album album = new Album(id, name, timeCount, thumbnail);
                                idsList.add(album.getId());
                                imageList.add(album.getThumbnail());
                                break;

                            case 1:
                                String id1 = dataObj.getString("id");
                                String name1 = dataObj.getString("a_title");
                                String thumbnail1 = dataObj.getString("audio");

                                Album album1 = new Album(id1, name1, timeCount, thumbnail1);
                                idsList.add(album1.getId());
                                imageList.add(album1.getThumbnail());

                                break;

                            case 2:
                                String id2 = dataObj.getString("id");
                                String name2 = dataObj.getString("a_title");
                                String thumbnail2 = dataObj.getString("video");

                                Album album2 = new Album(id2, name2, timeCount, thumbnail2);
                                idsList.add(album2.getId());
                                imageList.add(album2.getThumbnail());
                                break;
                        }

                    }

                    init();


                } catch (JSONException e) {
                    // JSON error
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


}

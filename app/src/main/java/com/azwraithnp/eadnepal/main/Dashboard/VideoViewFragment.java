package com.azwraithnp.eadnepal.main.Dashboard;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.azwraithnp.eadnepal.main.helper_classes.ExtendedTimeBar;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoViewFragment extends Fragment {


    TextView title, description;
    ImageView phone, email, website;

    ImageView audioIcon;
//    VideoView videoView;

    private SimpleExoPlayer player;
    private PlayerView playerView;

    boolean balanceTransferred = false;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private ComponentListener componentListener;
    ExtendedTimeBar defaultTimeBar;

    Album video;
    String transferUrl;

    String url;

    public VideoViewFragment() {
        // Required empty public constructor<?xml version="1.0" encoding="utf-8"?>
        //<FrameLayout
        //        android:id="@+id/frame"
        //        xmlns:android="http://schemas.android.com/apk/res/android"
        //        xmlns:tools="http://schemas.android.com/tools"
        //        xmlns:app="http://schemas.android.com/apk/res-auto"
        //        android:layout_width="match_parent"
        //        android:layout_height="match_parent"
        //        tools:context=".MainActivity">
        //
        //</FrameLayout>
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_view, container, false);

        Log.d("HERE", "HERE");

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.descText);
        phone = v.findViewById(R.id.phoneButton);
        email = v.findViewById(R.id.emailButton);
        website = v.findViewById(R.id.webButton);

        audioIcon = v.findViewById(R.id.soundIcon);

        video = new Gson().fromJson(getArguments().getString("video"), Album.class);

        Log.d("VIDEOURL", video.getThumbnail());
        Log.d("VIDEOURL", getArguments().getString("contentType"));

        if(getArguments().getString("contentType").equals("video"))
        {
            transferUrl = AppConfig.URL_TRANSFER_VIDEO;
            url = "https://eadnepal.com/client/pages/target video/uploads/" + video.getThumbnail();
            String reformedUrl = "";
            for(int i=0;i<url.length();i++)
            {
                if(url.charAt(i) == ' ')
                {
                    reformedUrl+="%20";
                }
                else
                {
                    reformedUrl+=url.charAt(i);
                }
            }
            url = reformedUrl;
            audioIcon.setVisibility(View.GONE);
        }
        else
        {
            transferUrl = AppConfig.URL_TRANSFER_AUDIO;
            url = "https://eadnepal.com/client/pages/target audio/uploads/" + video.getThumbnail();
            String reformedUrl = "";
            for(int i=0;i<url.length();i++)
            {
                if(url.charAt(i) == ' ')
                {
                    reformedUrl+="%20";
                }
                else
                {
                    reformedUrl+=url.charAt(i);
                }
            }
            url = reformedUrl;
            Log.d("AUDIOCHECK", reformedUrl);
            audioIcon.setVisibility(View.VISIBLE);
        }

//        videoView = v.findViewById(R.id.videoView);
        playerView = v.findViewById(R.id.videoView);
        defaultTimeBar = playerView.findViewById(R.id.exo_progress);
        defaultTimeBar.setEnabled(false);
        defaultTimeBar.setForceDisabled(true);

        title.setText(video.getName());

        Log.d("video_url", url);

        description.setText(video.getDesc());

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + video.getPhone().trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: " + video.getEmail().trim()));
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = video.getUrl().trim();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        componentListener = new ComponentListener();
//        videoView.setVideoPath(url);
//        videoView.requestFocus();
//        videoView.start();



//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                prog.dismiss();
//            }
//        });
//
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                transferBalance(getArguments().getString("videoId"), AppConfig.URL_TRANSFER_VIDEO, getArguments().getString("userId"));
//            }
//        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer(url);
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {


        super.onPause();
        if (Util.SDK_INT <= 23) {
          releasePlayer();
        }

//
    }

    @Override
    public void onStop() {

        super.onStop();

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    private void initializePlayer(String url) {

            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            player.addListener(componentListener);
            playerView.setPlayer(player);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);


        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        player.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {


        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab"))
                .createMediaSource(uri);
    }

    public void transferBalance(final String mediaId, final String url, final String userId)
    {
        String tag_string_req = "req_transfer";

        Log.d("transferLOG",  "Media id: " + mediaId + " Url: " + url + "UserID: " + userId);

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
                params.put("npl_id", userId);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    private class ComponentListener extends Player.DefaultEventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if(playWhenReady)
                    {
                        if(!balanceTransferred)
                        {
                            balanceTransferred = true;
                            transferBalance(video.getId(), transferUrl, getArguments().getString("userId"));
                        }
                    }
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }

        }
    }
}

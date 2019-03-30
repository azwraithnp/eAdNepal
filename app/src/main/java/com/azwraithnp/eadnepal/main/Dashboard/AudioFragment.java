package com.azwraithnp.eadnepal.main.Dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AudioFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<Album> audioList;

    private CardAdapter cardAdapter;

    private ProgressBar progressBar;

    boolean stopped = false;

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;

        Gson gson = new Gson();

        audioList = new ArrayList<>();

        final UserModel user = gson.fromJson(getArguments().getString("User"), UserModel.class);

        ((Dashboard)getActivity()).changeText("All Audio");

        View v = inflater.inflate(R.layout.fragment_audio, container, false);

        progressBar = v.findViewById(R.id.loadingProgressBar);

        recyclerView = v.findViewById(R.id.all_audio_recycler_view);

        cardAdapter = new CardAdapter(getActivity(), audioList, "audioall");

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && audioList.size() <=1) {
                    retrieveAudio(user, audioList.get(audioList.size()-1).getId());
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {
                        stopped = false;

                        if(audioList.get(position).getId().equals("8888"))
                        {

                        }
                        else {


                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.intromusic);
                            dialog.show();

                            Toast.makeText(getActivity(), "Now Playing..", Toast.LENGTH_SHORT).show();
                            TextView title = dialog.findViewById(R.id.title);
                            title.setText(audioList.get(position).getName());

                            String url = "http://eadnepal.com/client/pages/target%20audio/uploads/" + audioList.get(position).getThumbnail(); // your URL here
                            final MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(url);
                                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.start();

                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if(!stopped)
                                    {
                                        mediaPlayer.stop();
                                        mediaPlayer.release();
                                    }
                                }
                            });

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    dialog.cancel();
                                    mediaPlayer.release();
                                    stopped = true;
                                    Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
                                    transferBalance(audioList.get(position).getId(), AppConfig.URL_TRANSFER_AUDIO, user);
                                }
                            });
                        }
                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        retrieveAudio(user, "0");

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

    public void retrieveAudio(final UserModel user, final String aid)
    {
        progressBar.setVisibility(View.VISIBLE);
        String tag_string_req = "req_audio";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALL_AUDIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("Audio", "Audio Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject dataObj = jsonArray.getJSONObject(i);

                        String name = dataObj.getString("a_title");
                        int payOut = Integer.parseInt(dataObj.getString("reach_out_price"));
                        String audio = dataObj.getString("audio");

                        String id = dataObj.getString("id");

                        Album album = new Album(id, name, payOut, audio);
                        audioList.add(album);
                    }


                    cardAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    // JSON error
                    audioList.add(new Album("8888", "No more ads", 0, "abc"));
                    cardAdapter.notifyDataSetChanged();
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
                params.put("aid", aid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}

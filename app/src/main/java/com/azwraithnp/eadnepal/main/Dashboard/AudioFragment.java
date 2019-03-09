package com.azwraithnp.eadnepal.main.Dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

        UserModel user = gson.fromJson(getArguments().getString("User"), UserModel.class);

        View v = inflater.inflate(R.layout.fragment_audio, container, false);

        recyclerView = v.findViewById(R.id.all_audio_recycler_view);

        cardAdapter = new CardAdapter(getActivity(), audioList, "audioall");

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        if(position == cardAdapter.getItemCount() - 1) {
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
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    dialog.cancel();
                                    mediaPlayer.release();
                                    Toast.makeText(getActivity(), "Balance transferred!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        retrieveAudio(user);

        return v;
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


                    cardAdapter.notifyDataSetChanged();



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

    @Override
    public void onDetach() {
        super.onDetach();

    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}

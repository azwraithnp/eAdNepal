package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Album> imageList;

    private CardAdapter cardAdapter;

    private ProgressBar progress;

    private CountDownTimer countDownTimer;

    public PictureFragment() {
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

        imageList = new ArrayList<>();

        final UserModel user = gson.fromJson(getArguments().getString("User"), UserModel.class);

        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        recyclerView = v.findViewById(R.id.all_photo_recycler_view);

        progress = v.findViewById(R.id.loadingProgressBar);

        cardAdapter = new CardAdapter(getActivity(), imageList, "photoall");

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    retrieveImages(user, imageList.get(imageList.size()-1).getId());
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position)
                    {

                        String url = "http://eadnepal.com/client/pages/target/uploads/" + imageList.get(position).getThumbnail();

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.intropicture);
                        dialog.show();

                        ImageView img = dialog.findViewById(R.id.adPic);
                        Glide.with(getActivity()).load(url).into(img);

                        final ProgressBar progressBar=(ProgressBar)dialog.findViewById(R.id.progressbar);
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
                                transferBalance(imageList.get(position).getId(), AppConfig.URL_TRANSFER_PICTURE, user);
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

                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );

        retrieveImages(user, "0");

        return v;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

    public void retrieveImages(final UserModel user, final String id)
    {
        progress.setVisibility(View.VISIBLE);
        String tag_string_req = "req_images";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALL_IMAGES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Image", "Image Response: " + response.toString());

                try {

                    progress.setVisibility(View.INVISIBLE);

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
                        imageList.add(album);

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
                params.put("aid", id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
}

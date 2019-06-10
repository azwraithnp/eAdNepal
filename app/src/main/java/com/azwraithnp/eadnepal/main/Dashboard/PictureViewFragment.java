package com.azwraithnp.eadnepal.main.Dashboard;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureViewFragment extends Fragment {

    TextView title, description;
    TextView timeRemaining;
    ImageView phone, email, website;


    ImageView adPic;
    Album picture;

    CountDownTimer countDownTimer;

    public PictureViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_picture_view, container, false);

        picture = new Gson().fromJson(getArguments().getString("picture"), Album.class);

        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.descText);
        phone = v.findViewById(R.id.phoneButton);
        email = v.findViewById(R.id.emailButton);
        website = v.findViewById(R.id.webButton);
        timeRemaining = v.findViewById(R.id.pictureTitle);

        String url = "http://eadnepal.com/client/pages/target/uploads/" + picture.getThumbnail();

        adPic = v.findViewById(R.id.adPic);
        Glide.with(getActivity()).load(url).into(adPic);

        title.setText(picture.getName());
        description.setText(picture.getDesc());

        countDownTimer=new CountDownTimer(15000,1000) {

            int i=0;
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                int timeRem = (int)(millisUntilFinished/1000);
                timeRemaining.setText("Time Remaining: " + timeRem + " seconds");
            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                transferBalance(picture.getId(), AppConfig.URL_TRANSFER_PICTURE, getArguments().getString("userId"));
            }
        };


        countDownTimer.start();


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + picture.getPhone().trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: " + picture.getEmail().trim()));
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = picture.getUrl().trim();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null)
            countDownTimer.cancel();
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


}

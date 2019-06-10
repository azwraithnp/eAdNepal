package com.azwraithnp.eadnepal.main.Dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Adapters.InterestsAdapter;
import com.azwraithnp.eadnepal.main.Models.Interest;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.azwraithnp.eadnepal.main.helper_classes.GridSpacingItemDecoration;
import com.azwraithnp.eadnepal.main.helper_classes.RecyclerItemClickListener;
import com.bumptech.glide.Glide;
import com.hootsuite.nachos.tokenizer.ChipTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button nextSet, doneBtn;
    ArrayList<Interest> interestsList = new ArrayList<>();
    ArrayList<Integer> idsList = new ArrayList<>();
    InterestsAdapter adapter;

    ArrayList<Integer> clickedIds = new ArrayList<>();
    ArrayList<String> chosenInterests = new ArrayList<>();

    ArrayList<Interest> displaySet = new ArrayList<>();

    RelativeLayout mainView, loadingView;

    int setIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        adapter = new InterestsAdapter(this, displaySet);

        mainView = findViewById(R.id.mainView);
        loadingView = findViewById(R.id.loadingView);

        nextSet = findViewById(R.id.nextSet);
        doneBtn = findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Interest interest:
                     interestsList) {
                    if(clickedIds.contains(Integer.parseInt(interest.getId())))
                    {
                        chosenInterests.add(interest.getTitle());
                    }
                }

                if(chosenInterests.size() == 0)
                {
                    Toast.makeText(ChooseActivity.this, "Please choose atleast one interest", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent confirmIntent = new Intent(ChooseActivity.this, ConfirmChoose.class);
                    confirmIntent.putStringArrayListExtra("interestList", chosenInterests);
                    confirmIntent.putExtra("User", getIntent().getStringExtra("User"));
                    startActivity(confirmIntent);
                    finish();
                }


            }
        });

        nextSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIndex++;
                if(setIndex == 1)
                    generateSets(4,8);
                else
                {
                    generateSets(8, 12);
                }
            }
        });

        recyclerView = findViewById(R.id.interests_recycler_view);

        getInterests();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ChooseActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ChooseActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(final View view, final int position)
                    {
                        final int shortAnimationDuration = getResources().getInteger(
                                android.R.integer.config_longAnimTime);

                        int incrementCount = 0;

                        if(setIndex == 1)
                        {
                            incrementCount = 4;
                        }
                        else if(setIndex == 2)
                        {
                            incrementCount = 8;
                        }
                        clickedIds.add(incrementCount + idsList.get(position));

                        idsList.set(position, idsList.get(position) + 4);

                        view.animate()
                                .alpha(0f)
                                .setDuration(shortAnimationDuration)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        view.setVisibility(View.INVISIBLE);

                                        view.setVisibility(View.VISIBLE);

                                        ImageView imageView = view.findViewById(R.id.image2);

                                        String nextUrl = "";

                                        for (Interest interest:
                                             interestsList) {
                                            if(interest.getId().equals(String.valueOf(idsList.get(position))))
                                            {
                                                nextUrl = interest.getImage();
                                            }
                                        }

                                        Glide.with(ChooseActivity.this).load("http://eadnepal.com/management/pages/Add%20Interest/uploads/" + nextUrl).into(imageView);

                                        view.animate()
                                                .alpha(1f)
                                                .setDuration(shortAnimationDuration)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        view.setVisibility(View.VISIBLE);
                                                    }
                                                });

                                    }
                                });

                    }

                    @Override public void onLongItemClick(View view, int position)
                    {

                    }
                })
        );
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void generateSets(int first, int last)
    {
        displaySet.clear();

        for(int i=first;i<last;i++)
        {
            Interest interest = interestsList.get(i);

            if(!clickedIds.contains(Integer.parseInt(interest.getId())))
            {
                displaySet.add(interest);
            }

            adapter.notifyDataSetChanged();

        }

        if(displaySet.size() == 0)
        {
            setIndex++;
            generateSets(8, 12);
        }
    }

    public void getInterests()
    {
        String tag_string_req = "req_interests";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INTERESTS_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Interests", "Interests Response: " + response.toString());

                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);

                    String status = jObj.getString("status");
                    String status_message = jObj.getString("status_message");
                    JSONArray data = jObj.getJSONArray("data");

                    if(status.equals("200"))
                    {
                        for(int i=0;i<data.length();i++)
                        {
                            JSONObject jsonObject = data.getJSONObject(i);

                            Interest interest = new Interest(jsonObject.getString("id"), jsonObject.getString("title"), jsonObject.getString("image"));

                            interestsList.add(interest);

                        }

                        for (Interest interest:
                                interestsList) {
                            idsList.add(Integer.parseInt(interest.getId()));
                        }

                        generateSets(0, 4);

                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Interests", "Interests Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("ead_token", AppConfig.EAD_TOKEN);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void showDialog() {
        loadingView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
    }

    private void hideDialog(){
        loadingView.setVisibility(View.GONE);
        mainView.setVisibility(View.VISIBLE);
    }



}

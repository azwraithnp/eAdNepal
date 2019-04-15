package com.azwraithnp.eadnepal.main.Dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Login.MainActivity;
import com.azwraithnp.eadnepal.main.Models.Interest;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.google.gson.Gson;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmChoose extends AppCompatActivity {

    NachoTextView nachoTextView;
    Button confirmButton, backButton;
    RelativeLayout loadingView, mainView;

    String token = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_choose);

        nachoTextView = findViewById(R.id.nacho_text_view);
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);

        loadingView = findViewById(R.id.loadingView);
        mainView = findViewById(R.id.mainView);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = getIntent().getStringExtra("User");
                UserModel user = new Gson().fromJson(json, UserModel.class);

                saveInterests(token, user);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmChoose.this, ChooseActivity.class));
            }
        });

        Intent i = getIntent();
        ArrayList<String> chosenInterests = new ArrayList<>();
        chosenInterests = i.getStringArrayListExtra("interestList");

        nachoTextView.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);


        for (String text:
             chosenInterests) {
            token = token + text + ", ";
        }

        nachoTextView.setText(token);

    }

    public void saveInterests(final String interests, final UserModel user)
    {
        String tag_string_req = "req_interests";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INTERESTS_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                Log.d("Interests", "Interests Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    String status = jObj.getString("status");
                    String status_message = jObj.getString("status_message");
                    String data = jObj.getString("data");

                    if(status.equals("200"))
                    {
                        final Dialog dialog = new Dialog(ConfirmChoose.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.passchange_layout);
                        dialog.show();

                        Button passChangeButton = dialog.findViewById(R.id.changePasswordButton);
                        final EditText oldPasswordEnter = dialog.findViewById(R.id.oldPassword);
                        final EditText newPasswordEnter = dialog.findViewById(R.id.newPassword);
                        final EditText newPasswordConfirmEnter = dialog.findViewById(R.id.newPasswordConfirm);

                        SharedPreferences sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE);

                        final String oldPassword = sharedPreferences.getString("userPassword", "");

                        passChangeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(oldPasswordEnter.getText().toString().equals(oldPassword) && !oldPasswordEnter.getText().toString().isEmpty())
                                {
                                    if(newPasswordEnter.getText().toString().equals(newPasswordConfirmEnter.getText().toString()) && !newPasswordEnter.getText().toString().isEmpty() && !newPasswordConfirmEnter.getText().toString().isEmpty())
                                    {
                                        Toast.makeText(ConfirmChoose.this, "Please change your password for first-time login", Toast.LENGTH_SHORT).show();
                                        changePassword(user, newPasswordEnter.getText().toString());
                                    }
                                    else
                                    {
                                        newPasswordEnter.setError("Passwords do not match");
                                        newPasswordConfirmEnter.setError("Passwords do not match");
                                    }
                                }
                                else
                                {
                                    oldPasswordEnter.setError("Password does not match with your old password");
                                }
                            }
                        });
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
                params.put("uid", user.getId());
                params.put("interest", interests);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    public void changePassword(final UserModel user, final String newPassword)
    {
        String tag_string_req = "req_pwdchange";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PASSWORD_CHANGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Password change", "Password change Response: " + response.toString());

                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);

                    if(jObj.getString("status_message").equals("Logged In"))
                    {
                        Toast.makeText(ConfirmChoose.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", "");
                        editor.commit();

                        startActivity(new Intent(ConfirmChoose.this, MainActivity.class));
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

                Log.e("Password change", "Password change Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("npl_token", AppConfig.EAD_TOKEN);
                params.put("pwd", newPassword);
                params.put("uid", user.getId());
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

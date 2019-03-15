package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Login.MainActivity;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Switch autoLogin;
    private TextView changePassword;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final SharedPreferences mPrefs = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

        assert getArguments() != null;

        final UserModel user = new Gson().fromJson(getArguments().getString("User"), UserModel.class);

        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        autoLogin = v.findViewById(R.id.switchButton);
        changePassword = v.findViewById(R.id.passwordChangeText);

        autoLogin.setChecked(mPrefs.getBoolean("autologin", true));

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("autologin", isChecked);
                editor.apply();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.passchange_layout);
                dialog.show();

                Button passChangeButton = dialog.findViewById(R.id.changePasswordButton);
                final EditText oldPasswordEnter = dialog.findViewById(R.id.oldPassword);
                final EditText newPasswordEnter = dialog.findViewById(R.id.newPassword);
                final EditText newPasswordConfirmEnter = dialog.findViewById(R.id.newPasswordConfirm);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

                final String oldPassword = sharedPreferences.getString("userPassword", "");

                passChangeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(oldPasswordEnter.getText().toString().equals(oldPassword))
                        {
                            if(newPasswordEnter.getText().toString().equals(newPasswordConfirmEnter.getText().toString()))
                            {
                                Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();
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
        });

        return v;
    }

    public void changePassword(final UserModel user, final String newPassword)
    {
        String tag_string_req = "req_pwdchange";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PASSWORD_CHANGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Password change", "Password change Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

                    if(jObj.getString("status_message").equals("Logged In"))
                    {
                        Toast.makeText(getActivity(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", "");
                        editor.commit();

                        startActivity(new Intent(getActivity(), MainActivity.class));
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

}

package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Login.MainActivity;
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
public class ProfileFragment extends Fragment {

    ProgressDialog progressDialog;
    TextView name, balance, age, gender, emailV, phone, location, date, college, field, education, company, post;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        setupViews(v);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

        checkLogin(sharedPreferences.getString("userEmail", ""), sharedPreferences.getString("userPassword", ""));

        return v;
    }

    private void setupViews(View v)
    {
        name = v.findViewById(R.id.userName);
        balance = v.findViewById(R.id.balanceValue);
        emailV = v.findViewById(R.id.emailValue);
        age = v.findViewById(R.id.ageValue);
        gender = v.findViewById(R.id.genderValue);
        phone = v.findViewById(R.id.phoneValue);
        location = v.findViewById(R.id.locationValue);
        date = v.findViewById(R.id.dateValue);
        college = v.findViewById(R.id.collegeValue);
        field = v.findViewById(R.id.fieldValue);
        education = v.findViewById(R.id.educationValue);
        company = v.findViewById(R.id.companyValue);
        post = v.findViewById(R.id.postValue);
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.setMessage("Logging in ...");
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

                    name.setText(user.getF_name() + " " + user.getL_name());
                    balance.setText(user.getBalance());
                    age.setText(user.getAge());
                    gender.setText(user.getSex());
                    emailV.setText(user.getEmail());
                    phone.setText(user.getPhone());
                    location.setText(user.getLocation());
                    date.setText(user.getReg_date());
                    college.setText(user.getCollege());
                    field.setText(user.getFiled_of_study());
                    education.setText(user.getLevel());
                    company.setText(user.getCompany());
                    post.setText(user.getPost());

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Login", "Login Error: " + error.getMessage());
                hideDialog();

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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public String getString(JSONObject jsonObject, String stringToken) throws JSONException {
        return jsonObject.getString(stringToken);
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}

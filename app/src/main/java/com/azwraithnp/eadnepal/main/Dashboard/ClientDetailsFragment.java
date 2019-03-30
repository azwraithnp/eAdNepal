package com.azwraithnp.eadnepal.main.Dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.History;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientDetailsFragment extends Fragment {

    TextView nameField, emailField, phoneField, companyField, companyTypeField, companyPANField, estDateField, regDateField;

    AVLoadingIndicatorView avLoadingIndicatorView;
    ScrollView mainProfileView;
    RelativeLayout mainLoadingView;

    public ClientDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_client_details, container, false);

        nameField = v.findViewById(R.id.nameValue);
        emailField = v.findViewById(R.id.emailValue);
        phoneField = v.findViewById(R.id.phoneValue);
        companyField = v.findViewById(R.id.companyValue);
        companyTypeField = v.findViewById(R.id.coTypeValue);
        companyPANField = v.findViewById(R.id.companyPANValue);
        estDateField = v.findViewById(R.id.estDateValue);
        regDateField = v.findViewById(R.id.regDateValue);

        avLoadingIndicatorView = v.findViewById(R.id.avi);
        mainLoadingView = v.findViewById(R.id.loadingView);
        mainLoadingView.setVisibility(View.GONE);

        mainProfileView = v.findViewById(R.id.mainScrollView);

        getClientDetails(getArguments().getString("admin_email"), getArguments().getString("client_phone"));

        Button verifyButton  = v.findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyClient(getArguments().getString("admin_email"), getArguments().getString("client_phone"));
            }
        });

        return v;
    }

    private void getClientDetails(final String email, final String phoneValue) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLIENT_DETAILS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Login", "Login Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    JSONArray jsonArray = jObj.getJSONArray("data");

                    JSONObject dataObj = jsonArray.getJSONObject(0);

                    String fname = dataObj.getString("f_name");
                    String lname = dataObj.getString("l_name");
                    String email = dataObj.getString("email");
                    String phone = dataObj.getString("phone");
                    String company_name = dataObj.getString("company_name");
                    String company_type = dataObj.getString("company_type");
                    String company_pan = dataObj.getString("company_pan");
                    String est_date = dataObj.getString("est_date");
                    String reg_date = dataObj.getString("reg_date");

                    nameField.setText(fname + lname);
                    emailField.setText(email);
                    phoneField.setText(phone);
                    companyField.setText(company_name);
                    companyTypeField.setText(company_type);
                    companyPANField.setText(company_pan);
                    estDateField.setText(est_date);
                    regDateField.setText(reg_date);
                    hideDialog();


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
                params.put("admin_token", email);
                params.put("id", phoneValue);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void verifyClient(final String email, final String phone)
    {
        String tag_string_req = "req_verify";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_CLIENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Verify", "Verify Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);

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

                Log.e("Verify", "Verify Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email)
                params.put("admin_token", email);
                params.put("id", phone);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void showDialog() {
//        if (!progressDialog.isShowing())
//            progressDialog.show();
        mainLoadingView.setVisibility(View.VISIBLE);
        mainProfileView.setVisibility(View.GONE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).hideToolbar();
    }

    private void hideDialog() {
//        if (progressDialog.isShowing())
//            progressDialog.dismiss();
        mainLoadingView.setVisibility(View.GONE);
        mainProfileView.setVisibility(View.VISIBLE);
        if(getActivity() != null)
            ((Dashboard)getActivity()).showToolbar();
    }

}

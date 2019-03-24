package com.azwraithnp.eadnepal.main.Dashboard;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedeemFragment extends Fragment {

    TextView introText, currentBalance;
    EditText amountRedeem;
    RadioGroup radioGroup;
    RadioButton rechargeCardButton, giftVoucherButton, cashButton;

    Button redeemButton;

    String username, currentBalanceValue, redeemOption, redeemAmount, userId;

    ProgressDialog progressDialog;

    public RedeemFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_redeem, container, false);

        ((Dashboard)getActivity()).changeText("Redeem");

        setupViews(v);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

        checkLogin(sharedPreferences.getString("userEmail", ""), sharedPreferences.getString("userPassword", ""));

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getActivity(), "Please choose your redeem option before proceeding.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    switch (radioGroup.getCheckedRadioButtonId())
                    {
                        case R.id.recharge_card_button:
                            redeemOption = "Recharge Card";
                            break;

                        case R.id.gift_voucher_button:
                            redeemOption = "Gift Voucher";
                            break;

                        case R.id.cash_button:
                            redeemOption = "Cash";
                            break;
                    }

                    redeemAmount = amountRedeem.getText().toString();

                    if(redeemAmount.equals(""))
                    {
                        Toast.makeText(getActivity(), "Please enter an amount to redeem before proceeding.", Toast.LENGTH_SHORT).show();
                    }
                    else if(Float.parseFloat(currentBalanceValue) < 500)
                    {
                        Toast.makeText(getActivity(), "Your balance should atleast be 500 or more.", Toast.LENGTH_SHORT).show();
                    }
                    else if(Float.parseFloat(redeemAmount) > Float.parseFloat(currentBalanceValue))
                    {
                        Toast.makeText(getActivity(), "Insufficient balance in your account.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Pass", Toast.LENGTH_SHORT).show();
                        String userid = userId;
                        callRedeem(userid);
                    }
                }
            }
        });

        return v;
    }

    public void setupViews(View v)
    {
        introText = v.findViewById(R.id.introText);
        currentBalance = v.findViewById(R.id.balanceInfo);
        amountRedeem = v.findViewById(R.id.amountRedeem);
        radioGroup = v.findViewById(R.id.radioGroup);
        rechargeCardButton = v.findViewById(R.id.recharge_card_button);
        giftVoucherButton = v.findViewById(R.id.gift_voucher_button);
        cashButton = v.findViewById(R.id.cash_button);
        redeemButton = v.findViewById(R.id.redeem_button);
        progressDialog = new ProgressDialog(getActivity());
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

                    username = user.getF_name() + " " + user.getL_name();
                    currentBalanceValue = user.getBalance();
                    userId = user.getId();

                    introText.setText("Hello " + username + ",\nYou can choose to redeem your balance by options provided below.\n\nNote: Minimum balance should be 500 before you can redeem your balance.");
                    currentBalance.setText("Your current balance: " + currentBalanceValue);


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

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public String getString(JSONObject jsonObject, String stringToken) throws JSONException {
        return jsonObject.getString(stringToken);
    }

    public void callRedeem(final String userId)
    {
        String tag_string_req = "req_redeem";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REDEEM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Redeem", "Redeem Response: " + response.toString());

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

                Log.e("Redeem", "Redeem Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("ead_token", AppConfig.EAD_TOKEN);
                params.put("uid", userId);
                params.put("amount", redeemAmount);
                params.put("radio", redeemOption);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }



}

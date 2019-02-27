package com.azwraithnp.eadnepal.main.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Dashboard.Dashboard;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button loginButton, registerButton;
    EditText email, password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(email, password))
                {
                    checkLogin(email.getText().toString(), password.getText().toString());
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

    }

    public void setupViews()
    {
        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_reg);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);

        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);
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

                    String status = jObj.getString("status");
                    String status_message = jObj.getString("status_message");

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

                    if(Integer.parseInt(status) == 200 && status_message.equals("Logged In"))
                    {
                        startActivity(new Intent(MainActivity.this, Dashboard.class));
                        finish();
                    }
                    else if(Integer.parseInt(status) == 200 && status_message.equals("Account not found"))
                    {
                        Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
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

    public String getString(JSONObject jsonObject, String stringToken) throws JSONException {
        return jsonObject.getString(stringToken);
    }

    public boolean isEmpty(EditText ...edit)
    {
        for(EditText editText: edit)
        {
            String text = editText.getText().toString();
            if(text.isEmpty())
            {
                editText.setError("Field cannot be empty!");
                return true;
            }
        }

        return false;
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

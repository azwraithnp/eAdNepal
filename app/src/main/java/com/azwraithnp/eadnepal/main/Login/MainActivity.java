package com.azwraithnp.eadnepal.main.Login;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Dashboard.ChooseActivity;
import com.azwraithnp.eadnepal.main.Dashboard.Dashboard;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button loginButton, registerButton;
    EditText email;
    EditText password;
    ProgressDialog progressDialog;

    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("tag", "Key: " + key + " Value: " + value);
            }
        }

        new GetVersionCode().execute();


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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_password_layout);
                dialog.show();

                final EditText emailEnter = dialog.findViewById(R.id.emailEnter);
                Button submitEnter = dialog.findViewById(R.id.forgotButton);

                submitEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!emailEnter.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this, "Please wait..", Toast.LENGTH_SHORT).show();
                            forgotPasswordAPI(emailEnter.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please enter your email first..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

    }

    public class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            String currentVersion = null;
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show anything

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Updates provide fixes to critical bugs, performance and design improvements that help" +
                            "improve your experience in using our application. Please update to continue");
                    builder.setTitle("Update required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            finish();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            }

            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);


            SharedPreferences sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE);

            if(!(sharedPreferences.getString("user", "").equals("")) && sharedPreferences.getBoolean("autologin", true))
            {
                startActivity(new Intent(MainActivity.this, Dashboard.class).putExtra("User", sharedPreferences.getString("user", "")));
                finish();
            }

        }

    }


    public void setupViews()
    {
        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_reg);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        forgotPassword = findViewById(R.id.forgotPassword);

        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);
    }

    private void forgotPasswordAPI(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_forgot";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FORGOT_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Forgot", "Forgot Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    String status_message = jObj.getString("status_message");

                    String data = jObj.getString("data");

                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Forgot", "Forgot Error: " + error.getMessage());
                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
//                params.put("ead_tokan", AppConfig.EAD_TOKEN);
//                params.put("ead_email", email);
                params.put("npl_token", AppConfig.EAD_TOKEN);
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

                    if((Integer.parseInt(status) == 200 && status_message.equals("Account not found")))
                    {
                        Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    }

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

                    String userStatus = dataObj.getString("status");

                    Log.d("User", user.toString());
                    Log.d("User status", userStatus);

                    Gson gson = new Gson();
                    String json = gson.toJson(user);

                    SharedPreferences mPrefs = getSharedPreferences("userPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("user", json);
                    editor.putString("userEmail", email);
                    editor.putString("userPassword", password);
                    editor.apply();

                    if(Integer.parseInt(status) == 200 && status_message.equals("Logged In"))
                    {
                        if(userStatus.equals("1"))
                        {
                            startActivity(new Intent(MainActivity.this, Dashboard.class).putExtra("User", json));
                            finish();
                        }
                        else
                        {
                            startActivity(new Intent(MainActivity.this, ChooseActivity.class).putExtra("User", json));
                            finish();
                        }

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
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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



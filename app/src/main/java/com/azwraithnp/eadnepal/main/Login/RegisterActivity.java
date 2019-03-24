package com.azwraithnp.eadnepal.main.Login;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FloatingActionButton scrollButton;
    ScrollView scrollView;
    CustomNachoTextView nachoTextView;
    Button createAccount;
    CheckBox termsBox;

    EditText firstName, lastName, email, phone, age, colgName, companyName, postTitle;
    Spinner gender, location, education, fieldofstudy;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupViews();

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(firstName, lastName, email, phone, age))
                {
                    if(nachoTextView.getChipValues().size() == 0)
                    {
                        nachoTextView.setError("Please enter atleast one interest!");
                    }
                    else if(!termsBox.isChecked())
                    {
                        termsBox.setError("Please read and accept the terms and conditions before proceeding");
                    }
                    else
                    {
                        List<String> interests = nachoTextView.getChipValues();

                        registerUser(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), phone.getText().toString(), age.getText().toString(), gender.getSelectedItem().toString(), location.getSelectedItem().toString(), colgName.getText().toString(), education.getSelectedItem().toString(), fieldofstudy.getSelectedItem().toString(), companyName.getText().toString(), postTitle.getText().toString(), interests
                        );
                    }
                }
            }
        });

        String[] suggestions = new String[]{"Graphics Design", "Films", "Music", "Technology", "Medical", "Agriculture", "Hotels", "Bookings"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.choice_item_layout, suggestions);

        nachoTextView.setAdapter(adapter);

        nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);

        nachoTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nachoTextView.showDropDown();
                return false;
            }

        });

    }


    private void registerUser(final String firstNameText, final String lastNameText, final String emailText, final String phoneText, final String ageText, final String genderText, final String locationText, final String colgText, final String educationText, final String fieldText, final String companyText, final String postText, final List<String> interests) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final StringBuilder interestsSeparatedByCommas = new StringBuilder();

        for(String interest: interests)
        {
            interestsSeparatedByCommas.append(interest).append(",");
        }

        progressDialog.setMessage("Creating your account....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Register", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    String status = jObj.getString("status");
                    String status_message = jObj.getString("status_message");

                    Toast.makeText(RegisterActivity.this, status_message, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e("Register", "Register Error: " + error.getMessage());
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
                params.put("email", emailText);
                params.put("fname", firstNameText);
                params.put("lname", lastNameText);
                params.put("phone", phoneText);
                params.put("age", ageText);
                params.put("sex", genderText);
                params.put("field", fieldText);
                params.put("college_name", colgText);
                params.put("level", educationText);
                params.put("company", companyText);
                params.put("post", postText);
                params.put("location", locationText);
                params.put("interest", interestsSeparatedByCommas.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    public void setupViews()
    {
        scrollButton = findViewById(R.id.scrollDownButton);
        scrollView = findViewById(R.id.scrollView);
        nachoTextView = findViewById(R.id.nacho_text_view);
        createAccount = findViewById(R.id.btn_signup);
        firstName = findViewById(R.id.input_fname);
        lastName = findViewById(R.id.input_lname);
        email = findViewById(R.id.reg_input_email);
        phone = findViewById(R.id.input_phone);
        age = findViewById(R.id.input_age);
        colgName = findViewById(R.id.input_colg);
        companyName = findViewById(R.id.input_company);
        postTitle = findViewById(R.id.input_post);

        gender = findViewById(R.id.genderSpinner);
        location = findViewById(R.id.locationSpinner);
        education = findViewById(R.id.eduSpinner);
        fieldofstudy = findViewById(R.id.eduGenSpinner);

        termsBox = findViewById(R.id.link_terms);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);
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

package com.azwraithnp.eadnepal.main.Login;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.azwraithnp.eadnepal.R;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

public class RegisterActivity extends AppCompatActivity {

    FloatingActionButton scrollButton;
    ScrollView scrollView;
    NachoTextView nachoTextView;
    Button createAccount;

    EditText firstName, lastName, email, phone, age, colgName, companyName, postTitle;

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
                isEmpty(firstName, lastName, email, phone, age);
                if(nachoTextView.getChipValues().size() == 0)
                {
                    nachoTextView.setError("Please enter atleast one interest!");
                }
            }
        });

        String[] suggestions = new String[]{"Graphics Design", "Films", "Music", "Technology", "Medical", "Agriculture", "Hotels", "Bookings"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.choice_item_layout, suggestions);

        nachoTextView.setAdapter(adapter);

        nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
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
    }

    public void isEmpty(EditText ...edit)
    {
        for(EditText editText: edit)
        {
            String text = editText.getText().toString();
            if(text.isEmpty())
            {
                editText.setError("Field cannot be empty!");
            }
        }
    }
}

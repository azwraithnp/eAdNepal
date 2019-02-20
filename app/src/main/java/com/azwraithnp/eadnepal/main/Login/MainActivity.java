package com.azwraithnp.eadnepal.main.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.azwraithnp.eadnepal.R;

public class MainActivity extends AppCompatActivity {

    Button loginButton, registerButton;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmpty(email, password);
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

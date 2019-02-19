package com.azwraithnp.eadnepal.main.Login;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;

import com.azwraithnp.eadnepal.R;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

public class RegisterActivity extends AppCompatActivity {

    FloatingActionButton scrollButton;
    ScrollView scrollView;
    NachoTextView nachoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        scrollButton = findViewById(R.id.scrollDownButton);
        scrollView = findViewById(R.id.scrollView);
        nachoTextView = findViewById(R.id.nacho_text_view);

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        String[] suggestions = new String[]{"Graphics Design", "Films", "Music", "Technology", "Medical", "Agriculture", "Hotels", "Bookings"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.choice_item_layout, suggestions);

        nachoTextView.setAdapter(adapter);

        nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
    }
}

package com.azwraithnp.eadnepal.main.Dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.azwraithnp.eadnepal.R;

public class SampleActivity extends AppCompatActivity {

    CardView cardView, cardView2;
    ImageView imageView, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardinterests);

        cardView = findViewById(R.id.card_view);
        cardView2 = findViewById(R.id.card_view2);

        imageView = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);

        final int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cardView.setVisibility(View.INVISIBLE);
                                imageView.setImageResource(R.drawable.ic_baseline_home_24px);

                                cardView.setVisibility(View.VISIBLE);

                                cardView.animate()
                                        .alpha(1f)
                                        .setDuration(shortAnimationDuration)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                cardView.setVisibility(View.VISIBLE);


                                            }
                                        });

                            }
                        });

            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView2.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cardView2.setVisibility(View.INVISIBLE);
                                imageView2.setImageResource(R.drawable.medicine_square);

                                cardView2.setVisibility(View.VISIBLE);

                                cardView2.animate()
                                        .alpha(1f)
                                        .setDuration(shortAnimationDuration)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                cardView2.setVisibility(View.VISIBLE);


                                            }
                                        });
                            }
                        });

            }
        });




    }
}

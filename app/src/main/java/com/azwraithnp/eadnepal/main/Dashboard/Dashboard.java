package com.azwraithnp.eadnepal.main.Dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Login.MainActivity;
import com.azwraithnp.eadnepal.main.Login.RegisterActivity;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.azwraithnp.eadnepal.main.Models.UserModel;
import com.azwraithnp.eadnepal.main.helper_classes.AppConfig;
import com.azwraithnp.eadnepal.main.helper_classes.AppController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        homeButton = findViewById(R.id.home_button);



//        Gson gson = new Gson();

        String json = getIntent().getStringExtra("User");
//        UserModel user = gson.fromJson(json, UserModel.class);

        final Bundle bundle = new Bundle();
        bundle.putString("User", json);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
            }
        });

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_profile:

                                ProfileFragment profileFragment = new ProfileFragment();

                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, profileFragment).commit();

                                toolbar.setTitle("Profile");
                                break;

                            case R.id.nav_audio:

                                AudioFragment audioFragment = new AudioFragment();
                                audioFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, audioFragment).commit();

                                toolbar.setTitle("Audio");
                                break;

                            case R.id.nav_video:
                                VideoFragment videoFragment = new VideoFragment();
                                videoFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoFragment).commit();

                                toolbar.setTitle("Video");
                                break;

                            case R.id.nav_picture:

                                PictureFragment pictureFragment = new PictureFragment();
                                pictureFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, pictureFragment).commit();

                                toolbar.setTitle("Picture");
                                break;

                            case R.id.nav_redeem:
                                RedeemFragment redeemFragment = new RedeemFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, redeemFragment).commit();

                                toolbar.setTitle("Redeem");
                                break;

                            case R.id.nav_settings:
                                SettingsFragment settingsFragment = new SettingsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, settingsFragment).commit();
                                break;

                            case R.id.nav_logout:
                                SharedPreferences sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user", "");
                                editor.commit();

                                startActivity(new Intent(Dashboard.this, MainActivity.class));

                                break;



                        }

                       return true;
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}

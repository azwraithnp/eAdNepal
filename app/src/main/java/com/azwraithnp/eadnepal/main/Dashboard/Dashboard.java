package com.azwraithnp.eadnepal.main.Dashboard;

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
import android.view.MenuItem;
import android.widget.Toast;

import com.azwraithnp.eadnepal.R;

public class Dashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        HomeFragment homeFragment = new HomeFragment();
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
                            case R.id.nav_audio:
                                AudioFragment audioFragment = new AudioFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, audioFragment).commit();

                                toolbar.setTitle("Audio");
                                break;

                            case R.id.nav_video:
                                VideoFragment videoFragment = new VideoFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame, videoFragment).commit();

                                toolbar.setTitle("Video");
                                break;

                            case R.id.nav_picture:
                                PictureFragment pictureFragment = new PictureFragment();
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

                            case R.id.nav_logout:
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

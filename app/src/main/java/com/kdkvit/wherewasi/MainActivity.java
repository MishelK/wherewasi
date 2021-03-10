package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kdkvit.wherewasi.fragments.ActivityFragment;
import com.kdkvit.wherewasi.fragments.MainFragment;
import com.kdkvit.wherewasi.fragments.WelcomeFragment;
import com.kdkvit.wherewasi.services.LocationService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.UUID;

import models.User;

import static actions.actions.sendUserToBe;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    public static User user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MainFragment mainFragment;
    private View headerView;
    private ActivityFragment activityFragment;
    boolean Running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Running = getIntent().getBooleanExtra("working",false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        headerView = findViewById(R.id.main_header);

        //Init user from local storage
        user = SharedPreferencesUtils.getUser(this);
        if(user==null){ //User not exists -> welcome page
            WelcomeFragment welcomeFragment = new WelcomeFragment(name -> {
                user = new User(name, UUID.randomUUID().toString());
                sendUserToBe(MainActivity.this,user);
                initLoggedInUser();
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, welcomeFragment).commit();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);

        }else{ //User exists -> Main Page
            if(user.getId() == 0) { //User want not created in DB
                sendUserToBe(this, user);
            }
            initLoggedInUser();
        }


    }

    private void initLoggedInUser() {
        if (Build.VERSION.SDK_INT >=23){
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }else{
                if(!Running)
                    initService();
            }
        }else{
            if(!Running)
                initService();
        }
        headerView.setVisibility(View.VISIBLE);

        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();

        navigationView.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        ImageButton menuBtn = findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.home_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();
                        break;
                    case R.id.activity_Tab:
                        activityFragment = new ActivityFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, activityFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    private void initService() {
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("command", "app_created");
        startService(intent);
        Running = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Attention").setMessage("The application must have location permission in order for it to work").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).setCancelable(false).show();
            }
        }
    }

}
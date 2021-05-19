package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kdkvit.wherewasi.firebase.MyFirebaseMessagingService;
import com.kdkvit.wherewasi.fragments.ActivityFragment;
import com.kdkvit.wherewasi.fragments.ExportFragment;
import com.kdkvit.wherewasi.fragments.MainFragment;
import com.kdkvit.wherewasi.fragments.WelcomeFragment;
import com.kdkvit.wherewasi.services.LocationService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import models.Interaction;
import models.MyLocation;
import models.User;
import utils.CSVManager;
import utils.DatabaseHandler;
import utils.InteractionDatabaseHandler;
import utils.NotificationCenter;

import static actions.actions.sendUserToBe;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 42;
    private String [] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    public static User user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MainFragment mainFragment;
    private ExportFragment exportFragment;
    private View headerView;
    private ActivityFragment activityFragment;
    boolean Running;
    private ImageView headerIcon;
    private TextView headerText;
    private ImageView headerRightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Running = getIntent().getBooleanExtra("working", false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        headerView = findViewById(R.id.main_header);
        headerIcon = findViewById(R.id.header_image);
        headerText = findViewById(R.id.header_title);
        headerRightIcon = findViewById(R.id.header_right_icon);
        //Init user from local storage
        user = SharedPreferencesUtils.getUser(this);
        String fcmId = MyFirebaseMessagingService.getToken(this);
        if (user == null) { //User not exists -> welcome page
            WelcomeFragment welcomeFragment = new WelcomeFragment(name -> {
                user = new User(name, UUID.randomUUID().toString());
                user.setFcmId(fcmId);
                SharedPreferencesUtils.setUser(this, user);
                sendUserToBe(MainActivity.this, user);
                initLoggedInUser();
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, welcomeFragment).commit();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            navigationView.setVisibility(View.GONE);

        } else { //User exists -> Main Page
            if (user.getId() == 0) { //User was not created in DB
                sendUserToBe(this, user);
            }
            String userFcmId = user.getFcmId();
            if (!fcmId.isEmpty() && (userFcmId == null || !userFcmId.equals(fcmId))) {
                user.setFcmId(fcmId);
                sendUserToBe(this, user);
            }
            FirebaseMessaging.getInstance().subscribeToTopic("positives");

            initLoggedInUser();
        }
        requestAudioPermission();
    }

    private void initLoggedInUser() {
        if (Build.VERSION.SDK_INT >=23){
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasBluePermission = checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN);
            if(hasLocationPermission != PackageManager.PERMISSION_GRANTED || hasBluePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_ADMIN}, LOCATION_PERMISSION_REQUEST);
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
        TextView headerUserName = navigationView.getHeaderView(0).findViewById(R.id.drawer_header_user_name_tv);

        headerUserName.setText(user.getName());

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
                        headerText.setVisibility(View.GONE);
                        headerRightIcon.setVisibility(View.GONE);
                        headerIcon.setVisibility(View.VISIBLE);
                        break;
                    case R.id.activity_Tab:
                        activityFragment = new ActivityFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, activityFragment).commit();
                        headerText.setVisibility(View.VISIBLE);
                        headerText.setText(R.string.activity);
                        headerRightIcon.setVisibility(View.VISIBLE);
                        headerIcon.setVisibility(View.GONE);
                        headerRightIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activityFragment.openDrawer();
                            }
                        });
                        break;
                    case R.id.export_tab:
                        exportFragment = new ExportFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, exportFragment).commit();
                        headerText.setVisibility(View.VISIBLE);
                        headerText.setText(R.string.export);
                        headerRightIcon.setVisibility(View.GONE);
                        headerIcon.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
    }

    public void requestAudioPermission() {
        Log.i("SoniTalkService", "Audio permission has NOT been granted. Requesting permission.");
        // If an explanation is needed
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)) {
            Log.i("mainac","Displaying audio permission rationale to provide additional context.");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);

        } else {
            // First time, no explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION);
        }
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
        if(!checkGrantResults(permissions,grantResults)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Attention").setMessage("The application must have location and bluetooth permission in order for it to work").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }).setCancelable(false).show();
        }
        if (grantResults.length == 0) {
            //we will show an explanation next time the user click on start
            Toast.makeText(this, R.string.permissionRequestExplanation, Toast.LENGTH_SHORT).show();
        }
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "recived permissions", Toast.LENGTH_SHORT).show();
                }
                else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // This method checks if both location permission were granted in the request, returns true only if both were granted
    public boolean checkGrantResults(String[] permissions, int[] grantResults) {
        int granted = 0;

        if (grantResults.length > 0) {
            for(int i = 0; i < permissions.length ; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permission.equals(Manifest.permission.BLUETOOTH_ADMIN)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
            }
        } else { // if cancelled
            return false;
        }
        return granted == 2;
    }

}
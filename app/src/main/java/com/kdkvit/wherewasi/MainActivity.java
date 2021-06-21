package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kdkvit.wherewasi.firebase.MyFirebaseMessagingService;
import com.kdkvit.wherewasi.fragments.ActivityFragment;
import com.kdkvit.wherewasi.fragments.CommunicationFragment;
import com.kdkvit.wherewasi.fragments.ExportFragment;
import com.kdkvit.wherewasi.fragments.MainFragment;
import com.kdkvit.wherewasi.fragments.WelcomeFragment;
import com.kdkvit.wherewasi.services.LocationService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.UUID;

import models.Interaction;
import models.MyLocation;
import models.User;
import utils.DatabaseHandler;

import static Managers.ServerRequestManager.sendUserToBe;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 42;
    private String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

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
    private CommunicationFragment communicationFragment;

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
                String firebaseId = MyFirebaseMessagingService.getToken(this);
                user = new User(name, UUID.randomUUID().toString());
                user.setFcmId(firebaseId);
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
        //simInteractionData();
        //simLocationData();
    }

    private void initLoggedInUser() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasBluePermission = checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED || hasBluePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN}, LOCATION_PERMISSION_REQUEST);
            } else {
                if (!Running)
                    initService();
            }
        } else {
            if (!Running)
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
                    case R.id.communication_tab:
                        communicationFragment = new CommunicationFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, communicationFragment).commit();
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
            Log.i("mainac", "Displaying audio permission rationale to provide additional context.");

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
//        if(!checkGrantResults(permissions,grantResults)){
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("Attention").setMessage("The application must have location and bluetooth permission in order for it to work").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    startActivity(intent);
//                }
//            }).setCancelable(false).show();
//        }
//        if (grantResults.length == 0) {
//            //we will show an explanation next time the user click on start
//            Toast.makeText(this, R.string.permissionRequestExplanation, Toast.LENGTH_SHORT).show();
//        }
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "recived permissions", Toast.LENGTH_SHORT).show();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //Toast.makeText(this, "permissions denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // This method checks if both location permission were granted in the request, returns true only if both were granted
    public boolean checkGrantResults(String[] permissions, int[] grantResults) {
        int granted = 0;

        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
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

    public void simInteractionData() {
        DatabaseHandler db = new DatabaseHandler(this);

        int i = 0;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 1 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 2 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 3 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 4 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 5 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 6 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 7 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 8 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 10 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 11 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 12 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 13 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 14 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 15 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 16 + (10 * i), true, false, true));

        i = 1;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 17 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 18 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 19 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 111 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 112 + (10 * i), true, true, true));

        i = 2;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 113 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 114 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 115 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 116 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 117 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 118 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 119 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 121 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 122 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 123 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 124 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 125 + (10 * i), true, false, true));

        i = 3;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 126 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 127 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 128 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 129 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 131 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 132 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 133 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 134 + (10 * i), true, false, true));

        i = 4;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 135 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 136 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 137 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 138 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 139 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 141 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 142 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 144 + (10 * i), true, false, true));

        i = 5;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 145 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 146 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 147 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 148 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 149 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 151 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 152 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 153 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 154 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 155 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 156 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 157 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 158 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 159 + (10 * i), true, false, true));

        i = 6;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 161 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 162 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 163 + (10 * i), true, false, true));
        db.close();

        i = 7;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 21 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 22 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 23 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 24 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 995 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 996 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 799 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 998 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 999 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9910 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9911 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9912 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9913 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9914 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9915 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9916 + (10 * i), true, false, true));

        i = 8;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 9917 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 9918 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 9919 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99111 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99112 + (10 * i), true, false, true));

        i = 9;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99113 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99114 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99115 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99116 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99117 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 99118 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99119 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99121 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99122 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99123 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99124 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99125 + (10 * i), true, true, true));

        i = 10;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99126 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99127 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99128 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99129 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99131 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99132 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99133 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99134 + (10 * i), true, false, true));

        i = 11;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99135 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99136 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99137 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99138 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99139 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99141 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99142 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99144 + (10 * i), true, false, true));

        i = 12;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99145 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99146 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99147 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99148 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99149 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 99151 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99152 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99153 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99154 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99155 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99156 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99157 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99158 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99159 + (10 * i), true, false, true));

        i = 13;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99161 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99162 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99163 + (10 * i), true, false, true));
        db.close();
    }

    public void simLocationData() {
        DatabaseHandler db = new DatabaseHandler(this);
        int i = 0;
        db.addLocation(new MyLocation(1, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(2, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(3, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(4, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(5, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 1;
        db.addLocation(new MyLocation(6, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(7, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(8, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 2;
        db.addLocation(new MyLocation(9, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(11, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(12, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(13, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 3;
        db.addLocation(new MyLocation(14, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(15, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 4;
        db.addLocation(new MyLocation(16, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(17, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(18, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(19, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(121, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 5;
        db.addLocation(new MyLocation(122, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(123, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(124, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 6;
        db.addLocation(new MyLocation(125, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(126, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(127, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(128, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 7;
        db.addLocation(new MyLocation(91, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(92, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(93, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(94, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(95, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 8;
        db.addLocation(new MyLocation(96, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(97, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(98, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 9;
        db.addLocation(new MyLocation(99, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(911, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(912, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(913, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 10;
        db.addLocation(new MyLocation(914, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(915, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 11;
        db.addLocation(new MyLocation(916, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(917, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(918, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(919, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9121, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 12;
        db.addLocation(new MyLocation(9122, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9123, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9124, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 13;
        db.addLocation(new MyLocation(9125, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9126, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9127, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9128, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

    }
}
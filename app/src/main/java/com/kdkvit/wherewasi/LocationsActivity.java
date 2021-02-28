package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kdkvit.wherewasi.adapters.LocationsAdapter;
import com.kdkvit.wherewasi.adapters.LocationsTabsAdapter;
import com.kdkvit.wherewasi.fragments.MapsFragment;
import com.kdkvit.wherewasi.fragments.TimeLineFragment;
import com.kdkvit.wherewasi.services.LocationService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.MyLocation;
import models.User;
import utils.DatabaseHandler;

import static actions.actions.sendUserToBe;
import static com.kdkvit.wherewasi.services.LocationService.BROADCAST_CHANNEL;

public class LocationsActivity extends AppCompatActivity {

    final int LOCATION_PERMISSION_REQUEST = 1;

    BroadcastReceiver receiver;
    DatabaseHandler db;

    TimeLineFragment timeLineFragment = new TimeLineFragment();
    MapsFragment mapsFragment = new MapsFragment();

    public static List<MyLocation> locations = new ArrayList<>();

    boolean dbInit = false;
    Handler handler;
    boolean Running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_layout);
        User user = SharedPreferencesUtils.getUser(this);

        if(user == null || user.getId()==0){
            user = new User();
            user.setDeviceId(UUID.randomUUID().toString());
            sendUserToBe(this,user);
        }
        Running = getIntent().getBooleanExtra("working",false);

        handler = new Handler();
        db = new DatabaseHandler(this);

        initReceiver();

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

        LocationsTabsAdapter locationsTabsAdapter = new LocationsTabsAdapter(getSupportFragmentManager(),1);
        ViewPager viewPager = (ViewPager) findViewById(R.id.locations_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.locations_tab_layout);

        locationsTabsAdapter.addFragment(mapsFragment,"Map");
        locationsTabsAdapter.addFragment(timeLineFragment,"TimeLine");

        viewPager.setAdapter(locationsTabsAdapter);
        tabLayout.setupWithViewPager(viewPager);

        getLocationsHistory();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(BROADCAST_CHANNEL);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                if (command != null) {
                    switch (command) {
                        case "location_changed":
                            if(dbInit) {
                                MyLocation location = (MyLocation) intent.getSerializableExtra("location");
                                locations.add(0,location);
                                timeLineFragment.updateTimeLineAdapter();
                                mapsFragment.setMapPointers();
                            }
                            break;
                        case "close":
                            finish();
                            break;
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

    }

    private  void getLocationsHistory(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                locations = db.getAllLocations(DatabaseHandler.SORTING_PARAM.LastUpdated);

                dbInit = true;
                handler.post(()-> {
                    timeLineFragment.updateTimeLineAdapter();
                    mapsFragment.setMapPointers();
                });
            }
        }.start();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationsActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


}
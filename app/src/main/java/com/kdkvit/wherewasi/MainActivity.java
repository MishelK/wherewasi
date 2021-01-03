package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.kdkvit.wherewasi.LocationService.BROADCAST_CHANNEL;

public class MainActivity extends AppCompatActivity {

    final int LOCATION_PERMISSION_REQUEST = 1;

    BroadcastReceiver receiver;
    DatabaseHandler db;

    public static List<MyLocation> locations = new ArrayList<>();

    boolean dbInit = false;

    Handler handler;
    LocationsAdapter locationsAdapter;

    boolean Running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        RecyclerView recyclerView = findViewById(R.id.reclyer);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationsAdapter = new LocationsAdapter();


        recyclerView.setAdapter(locationsAdapter);

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
                                locationsAdapter.notifyDataSetChanged();
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
                handler.post(()->locationsAdapter.notifyDataSetChanged());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


}
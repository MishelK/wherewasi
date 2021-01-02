package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URI;

import static com.kdkvit.wherewasi.LocationService.BROADCAST_CHANNEL;

public class MainActivity extends AppCompatActivity {

    final int LOCATION_PERMISSION_REQUEST = 1;

    BroadcastReceiver receiver;

    boolean Running;
    TextView mainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >=23){
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
        }

        initReceiver();

        Running = getIntent().getBooleanExtra("working",false);

        if(!Running)
            initService();

        mainTv = findViewById(R.id.main_text_view);

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
                            MyLocation location = (MyLocation) intent.getSerializableExtra("location");
                            Toast.makeText(context, "Location changed: " + location.getLongitude() + " , " + location.getLatitude(), Toast.LENGTH_LONG).show();
                            mainTv.setText("Location: " + location.getLongitude() + " , " + location.getLatitude());
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
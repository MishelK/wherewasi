package com.kdkvit.wherewasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MainActivity extends AppCompatActivity {

    static final int BLE_REQ_CODE = 1;

    BLEManager bleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking for BLE scan required permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED)
            requestBlePermissions(BLE_REQ_CODE);
        //Checking if location services are enables, this is required in order to use BLE
        if(!checkLocationServicesEnabled(this)){
            //Need to add request to enable location in case it is not enabled
        }

        bleManager = new BLEManager();
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasBlePermissions()) {
                    if (bleManager.isScanning()) {
                        bleManager.stopScan();
                        btnScan.setText("Start Scanning");
                    } else {
                        bleManager.startScan();
                        btnScan.setText("Stop Scanning");
                    }
                }
            }
        });

        Button btnAdvertise = findViewById(R.id.btnAdvertise);
        btnAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasBlePermissions()) {
                    if(bleManager.isAdvertising()){
                        bleManager.stopAdvertising();
                        btnAdvertise.setText("Start Advertising");
                    } else {
                        bleManager.startAdvertising();
                        btnAdvertise.setText("Stop Advertising");
                    }
                }
            }
        });
    }


    // METHODS BELOW ARE FOR CHECKING REQUIRED PERMISSIONS IN ORDER TO USE BLE
    // In order to scan for devices using BLE, location services must be on, this method will be used to check if location is enabled
    public boolean checkLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check for ble scan permissions
    public boolean hasBlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    // Request ble permissions
    public void requestBlePermissions(int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN},
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!checkGrantResults(permissions, grantResults)) { // case permission was not granted
            requestBlePermissions(BLE_REQ_CODE);
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
    /////////////////////////////////////////////////////////////////////////
}
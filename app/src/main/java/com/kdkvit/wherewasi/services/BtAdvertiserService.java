package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.UUID;

import models.User;

public class BtAdvertiserService extends Service {

    private static final String SERVICE_IDENTIFIER = "wwi";

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        if (command != null) {
            switch (command) {
                case "start":
                    startAdvertising();
                    break;
                case "stop":
                    stopAdvertising();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopAdvertising();
        super.onDestroy();
    }

    /**
     * Callback for BLE advertising
     */
    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

            Log.i("BLE","Advertising started successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);

            Log.i("BLE", "Advertising failed to start");
            Log.i("BLE", "Error code: " + errorCode);
        }
    };

    /**
     * Starts BLE advertising with Unique service identifier
     */
    public void startAdvertising(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
            if (bluetoothAdapter.isMultipleAdvertisementSupported() && bluetoothAdapter.isEnabled()) { //Device supports Bluetooth LE and Bluetooth is enabled

                bluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

                AdvertiseSettings settings = new AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                        .setConnectable(true)
                        .setTimeout(0) // Limit advertising to a given amount of time A value of 0 will disable the time limit
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                        .build();

                AdvertiseData dataUUID = new AdvertiseData.Builder()
                        .setIncludeDeviceName(false)
                        .setIncludeTxPowerLevel(false)
                        .addServiceData(new ParcelUuid(getDeviceUUID()) , SERVICE_IDENTIFIER.getBytes())
                        .build();

                bluetoothLeAdvertiser
                        .startAdvertising(settings, dataUUID, advertiseCallback);
            }
        }
    }

    /**
     * Stops BLE advertising
     */
    public void stopAdvertising(){

        Log.i("BLE","Advertising Stopped");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            try {
                bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
                bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            }catch (Exception e) {

            }
        }
    }

    /**
     * Returns device's UUID or generates one if there is no UUID stored
     */
    private UUID getDeviceUUID() {
        User user = SharedPreferencesUtils.getUser(this);
        return UUID.fromString(user.getDeviceId());
    }

}

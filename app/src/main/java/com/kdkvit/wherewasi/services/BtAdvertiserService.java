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

import java.util.UUID;

public class BtAdvertiserService extends Service {

    private static final String serviceUUID = "00001810-0000-1000-8000-a0805f9b34fb";
    private static final String SERVICE_IDENTIFIER = "wwi";
    private static final String SP_UUID = "wwi_device_uuid";

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
                        //.addManufacturerData(1,"00001810-0000-1000-80000".getBytes())
                        //.addServiceUuid(new ParcelUuid(UUID.fromString(serviceUUID)))
                        .addServiceData(new ParcelUuid(getDeviceUUID()) , SERVICE_IDENTIFIER.getBytes())
                        .build();

                bluetoothLeAdvertiser
                        .startAdvertising(settings, dataUUID, advertiseCallback);
            }
        }
    }

    public void stopAdvertising(){

        Log.i("BLE","Advertising Stopped");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }
    }

    // Returns device's UUID or generates one if there is no UUID stored
    private UUID getDeviceUUID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uuid_string = preferences.getString(SP_UUID, null);

        if(uuid_string != null) // Case uuid was already generated before, return it
            return UUID.fromString(uuid_string);

        // Generating new random UUID and saving it to SP
        UUID uuid = UUID.randomUUID();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SP_UUID, uuid.toString());
        editor.apply();
        return  uuid;
    }

}

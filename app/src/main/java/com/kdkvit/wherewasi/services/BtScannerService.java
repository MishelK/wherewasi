package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import models.BtDevice;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BtScannerService extends Service {

    private static final String serviceUUID = "00001810-0000-1000-8000-a0805f9b34fb";
    private static final String SERVICE_IDENTIFIER = "wwi";
    private static final int IDLE_DURATION = 10000;
    private static final int CHECK_IDLE_DELAY = 10000;

    private HashMap<String, BtDevice> btDevices;

    private Timer timer;
    private Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() { //Called once when service is instantiated
        super.onCreate();
        btDevices = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //Called from startService()
        String command = intent.getStringExtra("command");
        if (command != null) {
            switch (command) {
                case "start":
                    startScan();
                    break;
                case "stop":
                    stopScan();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopScan();
        super.onDestroy();
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);

            Map<ParcelUuid, byte[]> uuidsMap = result.getScanRecord().getServiceData();

            // Checking if map contains "wwi" in order to confirm device is an app user
            String device_uuid = null;
            if (uuidsMap != null) {
                List<ParcelUuid> serviceIds = new ArrayList<>(uuidsMap.keySet());
                for (int i = 0; i < serviceIds.size(); i++) {
                    ParcelUuid uuid = serviceIds.get(i);
                    if (uuidsMap.containsKey(uuid)) {
                        String serviceData = new String(uuidsMap.get(uuid), StandardCharsets.UTF_8);
                        if (serviceData.equals(SERVICE_IDENTIFIER)) // Checking if included service data matches our identifier "wwi"
                            device_uuid = uuid.toString();
                    }
                }
            }

            if(device_uuid != null) { // Only if device had "wwi" in service data
                if (btDevices.containsKey(device_uuid)) { // Case device already in list
                    btDevices.get(device_uuid).setLastSeen(System.currentTimeMillis());
                } else { // Case device first seen
                    BtDevice device = new BtDevice();
                    device.setFirstSeen(System.currentTimeMillis());
                    device.setLastSeen(System.currentTimeMillis());
                    device.setUuid(device_uuid);
                    btDevices.put(device_uuid, device);
                }
            }
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (int i = 0; i < results.size(); i++) {

                Map<ParcelUuid, byte[]> uuidsMap = results.get(i).getScanRecord().getServiceData();

                // Checking if map contains "wwi" in order to confirm device is an app user
                String device_uuid = null;
                if (uuidsMap != null) {
                    List<ParcelUuid> serviceIds = new ArrayList<>(uuidsMap.keySet());
                    for (int j = 0; j < serviceIds.size(); j++) {
                        ParcelUuid uuid = serviceIds.get(j);
                        if (uuidsMap.containsKey(uuid)) {
                            String serviceData = new String(uuidsMap.get(uuid), StandardCharsets.UTF_8);
                            if (serviceData.equals(SERVICE_IDENTIFIER))
                                device_uuid = uuid.toString();
                        }
                    }
                }

                if(device_uuid != null) { // Only if device had "wwi" in service data
                    if (btDevices.containsKey(device_uuid)) { // Case device already in list
                        btDevices.get(device_uuid).setLastSeen(System.currentTimeMillis());
                    } else { // Case device first seen
                        BtDevice device = new BtDevice();
                        device.setFirstSeen(System.currentTimeMillis());
                        device.setLastSeen(System.currentTimeMillis());
                        device.setUuid(device_uuid);
                        btDevices.put(device_uuid, device);
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("BLE","scan failed");
        }
    };

    public void startScan(){

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();

        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(5000)
                .setUseHardwareBatchingIfSupported(true)
                .build();

        List<ScanFilter> filters = new ArrayList<>();

        scanner.startScan(filters, settings, scanCallback);
        //scanner.startScan(scanCallback);

        Log.i("BLE","Start Scan");

        //Schedule checkIdleConnections
        handler = new Handler();
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        checkIdleConnections();
                    }
                });
            }

            ;
        };
        timer.schedule(timerTask, CHECK_IDLE_DELAY, CHECK_IDLE_DELAY);
    }

    public void stopScan() {

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        Log.i("BLE","Stop Scan");

        // Stopping idle checks
        if(timer != null)
            timer.cancel();
    }

    // Iterates over devices and checks for devices last seen more than 5 minutes ago, in order to close the connection and log duration spent together
    private void checkIdleConnections() {
        Long currentTime = System.currentTimeMillis();
        Log.i("BLE", "Checking for idle devices");
        Log.i("BLE", "Devices in map:" + btDevices.toString());
        for (BtDevice device : btDevices.values()){
            if(currentTime - device.getLastSeen() >= IDLE_DURATION){ // Case device last seen longer than IDLE_DURATION
                // Here we would want to remove device from the map and in case user was in contact for long enough, log contact in database
                btDevices.remove(device.getUuid());
                Log.i("BLE", "Removing idle device from map: " + device.toString());
            }
        }
    }

}

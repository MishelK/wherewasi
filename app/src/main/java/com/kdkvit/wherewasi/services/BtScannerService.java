package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import com.kdkvit.wherewasi.BuildConfig;
import com.kdkvit.wherewasi.utils.Configs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.Interaction;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import utils.DatabaseHandler;

public class BtScannerService extends Service {

    private static final String SERVICE_IDENTIFIER = "wwi";
    public static final int IDLE_DURATION = Configs.IDLE_DURATION; // If a bluetooth device has not been seen for longer than this value, interaction will be closed
    public static final int CHECK_IDLE_DELAY = 1000; // Delay for interval checking for idle devices
    public static final int CONTACT_DURATION = Configs.CONTACT_DURATION; // Upon closing an interaction, if the interaction lasted longer than this value, it will be logged in the database
    public static final String BLE_SCANNING_CHANNEL = "wwiblescanning";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() { //Called once when service is instantiated
        super.onCreate();
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
            ArrayList<Interaction> btInteractions = new ArrayList<>();

            // Checking if map contains "wwi" in order to confirm device is an app user
            String device_uuid = null;
            if (uuidsMap != null) {
                List<ParcelUuid> serviceIds = new ArrayList<>(uuidsMap.keySet());
                for (int i = 0; i < serviceIds.size(); i++) {
                    ParcelUuid uuid = serviceIds.get(i);
                    if (uuidsMap.containsKey(uuid)) {
                        String serviceData = new String(uuidsMap.get(uuid), StandardCharsets.UTF_8);
                        if (serviceData.equals(SERVICE_IDENTIFIER)) // Checking if included service data matches our identifier "wwi"
                            device_uuid = uuid.getUuid().toString();
                    }
                }
            }

            if(device_uuid != null) { // Only if device had "wwi" in service data
                    Date now = new Date();
                    Interaction interaction = new Interaction();
                    interaction.setFirstSeen(now.getTime());
                    interaction.setLastSeen(now.getTime());
                    interaction.setUuid(device_uuid);
                    Log.i("BLE", "Adding device to btInteractions : " + interaction.getUuid());
                    btInteractions.add(interaction);
            }

            sendListInBroadcast(btInteractions);
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            ArrayList<Interaction> btInteractions = new ArrayList<>();
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
                                device_uuid = uuid.getUuid().toString();
                        }
                    }
                }

                if(device_uuid != null) {
                    Date now = new Date();
                    Interaction interaction = new Interaction();
                    interaction.setFirstSeen(now.getTime());
                    interaction.setLastSeen(now.getTime());
                    interaction.setUuid(device_uuid);
                    Log.i("BLE", "Adding device to btInteractions : " + interaction.getUuid());
                    btInteractions.add(interaction);
                }
            }
            sendListInBroadcast(btInteractions);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("BLE","scan failed");
        }
    };

    private void sendListInBroadcast(ArrayList<Interaction> btInteractions) {
        Intent receiverIntent = new Intent(BLE_SCANNING_CHANNEL);
        receiverIntent.putParcelableArrayListExtra("ble_list", btInteractions);
        LocalBroadcastManager.getInstance(BtScannerService.this).sendBroadcast(receiverIntent);
    }

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

        Log.i("BLE","Start Scan");

    }

    public void stopScan() {

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        Log.i("BLE","Stop Scan");

    }

    public void RssiTest(int rssi, int txPow) {
        Log.i("rssi", "Rssi: " + rssi + "");
        Log.i("rssi", "TxPow: " + txPow + "");

        double arg = (-69 - (rssi)) / (10 * 4);
        double dist = Math.pow(10, arg);
        Log.i("rssi", "Distance: " + dist);
    }
    

}

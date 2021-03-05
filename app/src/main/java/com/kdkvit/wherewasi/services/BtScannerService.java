package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BtScannerService extends Service {

    private static final String serviceUUID = "00001810-0000-1000-8000-a0805f9b34fb";

    private BluetoothLeScannerCompat scanner;

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
        super.onDestroy();
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("BLE", "Scan Result:");

            //String toString = result.getDevice().toString();
            String mac = result.getDevice().getAddress();
            int rssi = result.getRssi();

            if (mac != null)
                Log.i("BLE","MAC: " + mac);
            else
                Log.i("BLE", "ERROR: mac null");

            Log.i("BLE", "Rssi: " + Integer.toString(rssi));
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (int i = 0; i < results.size(); i++) {
                Log.i("BLE", "Scan Result:");

                //String toString = results.get(i).getDevice().toString();
                String mac = results.get(i).getDevice().getAddress();
                int rssi = results.get(i).getRssi();

                if (mac != null)
                    Log.i("BLE","MAC: " + mac);
                else
                    Log.i("BLE", "ERROR: mac null");

                Log.i("BLE", "Rssi: " + Integer.toString(rssi));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("BLE","scan failed");
        }
    };

    public void startScan(){

        scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(true)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        UUID MY_SERVICE_UUID = UUID.fromString(serviceUUID);
        UUID[] serviceUUIDs = new UUID[]{MY_SERVICE_UUID};
        for (UUID serviceUUID : serviceUUIDs){
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(serviceUUID))
                    .build();
            filters.add(filter);
        }

        scanner.startScan(filters, settings, scanCallback);
        Log.i("BLE","Start Scan");
    }

    public void stopScan() {

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        Log.i("BLE","Stop Scan");
    }

}

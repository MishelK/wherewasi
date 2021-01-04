package com.kdkvit.wherewasi;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class BLEManager {

    private BluetoothLeScannerCompat scanner;
    private boolean isScanning = false;

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("BLE", "Scan Result:");
            String deviceName = result.getDevice().getName();
            if (deviceName != null)
                Log.i("BLE", deviceName);
            else
                Log.i("BLE", "ERROR: device name null");

            String toString = result.getDevice().toString();
                    //result.toString();
            if (toString != null)
                Log.i("BLE", toString);
            else
                Log.i("BLE", "ERROR: toString null");


        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (int i = 0; i < results.size(); i++) {
                Log.i("BLE", "Scan Result:");
                String deviceName = results.get(i).getDevice().getName();
                if (deviceName != null)
                    Log.i("BLE", deviceName);
                else
                    Log.i("BLE", "ERROR: device name null");

                String toString = results.get(i).getDevice().toString();
                        //results.get(i).toString();
                if (toString != null)
                    Log.i("BLE", toString);
                else
                    Log.i("BLE", "ERROR: toString null");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("BLE","scan failed");
        }
    };

    public BLEManager() {
    }

    public void startScan(){
        isScanning = true;

        scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(true)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().build());
        scanner.startScan(filters, settings, scanCallback);
        Log.i("BLE","Start Scan");
    }

    public void stopScan() {
        isScanning = false;

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        Log.i("BLE","Stop Scan");
    }

    public boolean isScanning() {
        return isScanning;
    }

}

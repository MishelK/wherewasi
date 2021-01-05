package com.kdkvit.wherewasi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.ParcelUuid;
import android.provider.SyncStateContract;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class BLEManager {

    private static final int REQUEST_ENABLE_BT = 5;

    private BluetoothLeScannerCompat scanner;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private boolean isScanning = false;
    private boolean isAdvertising = false;

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

            //ScanRecord scanRecord = result.getScanRecord();
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

    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

            if(isAdvertising)
                Log.i("BLE","Advertising started successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);

            if(isAdvertising) {
                Log.i("BLE", "Advertising failed to start");
                Log.i("BLE", "Error code: " + errorCode);
            }
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
        UUID MY_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-a0805f9b34fb");
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
        isScanning = false;

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);
        Log.i("BLE","Stop Scan");
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void startAdvertising(){
        isAdvertising = true;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
            if (bluetoothAdapter.isMultipleAdvertisementSupported() && bluetoothAdapter.isEnabled()) { //Device supports Bluetooth LE and Bluetooth is enabled

                bluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

                AdvertiseSettings settings = new AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                        .setConnectable(true)
                        .setTimeout(0) // Limit advertising to a given amount of time A value of 0 will disable the time limit
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                        .build();

                AdvertiseData data = new AdvertiseData.Builder()
                        .setIncludeDeviceName(false)
                        .setIncludeTxPowerLevel(false)
                        .addServiceUuid(new ParcelUuid(UUID.fromString("00001810-0000-1000-8000-a0805f9b34fb")))
                        .build();

                bluetoothLeAdvertiser
                        .startAdvertising(settings, data, advertiseCallback);
            }
        }
    }

    public void stopAdvertising(){
        isAdvertising = false;
        Log.i("BLE","Advertising Stopped");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }
    }

    public boolean isAdvertising(){ return isAdvertising;}
}

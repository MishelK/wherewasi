package com.kdkvit.wherewasi.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kdkvit.wherewasi.MainActivity;
import com.kdkvit.wherewasi.R;

import models.Interaction;
import models.MyLocation;
import utils.DatabaseHandler;

import static com.kdkvit.wherewasi.services.BtScannerService.CONTACT_DURATION;
import static com.kdkvit.wherewasi.services.BtScannerService.IDLE_DURATION;

public class LocationService extends Service {
    public static final String BROADCAST_CHANNEL = "WhereWasI Broadcast";
    private static final int SIGNIFICANT_TIME = 1000 * 60 * 30;
    private static final String CHANNEL_NAME = "WhereWasSI Channel";
    private static final int TIME_CHECK_ACTIVE = 11 * 1000* 60;
    private static final long ADVERTISING_DELAY = 30 * 1000;
    private static final long SCANNING_DELAY = 1 * 60 * 1000;
    public LocationManager mlocManager;
    public MyLocationListener listener;
    public MyLocation previousBestLocation = null;
    private HashMap<String, Interaction> btInteractions =new HashMap<>();

    DatabaseHandler db;
    Geocoder geocoder;

    boolean isScanning, isAdvertising = false;

    NotificationManager manager;
    NotificationCompat.Builder builder;
    String channelId = "KDKVIT_NOTIF_CHANNEL";
    final int NOTIF_ID = 1;
    private Timer locationsTimer = new Timer();
    private Timer advertisingTimer = new Timer();
    private Timer scanningTimer = new Timer();
    private BroadcastReceiver scannerBroadcast;

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this, channelId);
        builder.setNotificationSilent();

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notif_layout);

        Intent closeIntent = new Intent(this, LocationService.class);
        closeIntent.putExtra("command", "close");
        PendingIntent closePendingIntent = PendingIntent.getService(this, 1, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.stop_service_btn, closePendingIntent);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("working", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setCustomContentView(remoteViews);

        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);

        startForeground(NOTIF_ID, builder.build());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        if (command != null) {
            switch (command) {
                case "app_created":
                    initLocation();
//                return START_STICKY;
                    break;
                case "close":
                    close();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void close() {
        stopSelf();
        Intent receiverIntent = new Intent(BROADCAST_CHANNEL);
        receiverIntent.putExtra("command", "close");
        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(receiverIntent);
    }


    public void initLocation() {
        db = new DatabaseHandler(this); // init database handler

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No premissions", Toast.LENGTH_SHORT).show();
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5*60*1000, 0, (LocationListener) listener);
        if(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5*60*1000, 0, (LocationListener) listener);
        }else{
            //Toast.makeText(this, "Failed to use gps...", Toast.LENGTH_SHORT).show();
        }

        locationsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("test","timer test");
                if(previousBestLocation!=null && previousBestLocation.getId() > 0){
                    previousBestLocation.setEndTime(System.currentTimeMillis());
                    previousBestLocation.setUpdateTime(System.currentTimeMillis());
                    sendLocationEvent(previousBestLocation);
                }
            }
        }, 0,30*1000);


        advertisingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isAdvertising){
                    stopBLE();
                    isAdvertising = false;
                }
                else{
                    startBLE();
                    isAdvertising = true;
                }
            }
        },5000,ADVERTISING_DELAY);

        IntentFilter filter = new IntentFilter(BtScannerService.BLE_SCANNING_CHANNEL);
        scannerBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Interaction> interactions = intent.getParcelableArrayListExtra("ble_list");
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Log.i("BLE", "Received interactions list from service : " + interactions);
                        addNewInteractions(interactions);
                    }
                };
                thread.start();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(scannerBroadcast,filter);
        scanningTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isScanning) {
                    stopScanning();
                    checkIdleConnections();
                    isScanning = false;
                }
                else {
                    startScanning();
                    isScanning = true;
                }
            }
        },5000,SCANNING_DELAY);

        geocoder = new Geocoder(this);
    }

    private void addNewInteractions(ArrayList<Interaction> interactions) {

        for(Interaction interaction : interactions){

            if(btInteractions.containsKey(interaction.getUuid())){
                btInteractions.get(interaction.getUuid()).setLastSeen(System.currentTimeMillis());
            }else{
                btInteractions.put(interaction.getUuid(),interaction);
            }
        }
    }

    private void startScanning(){
        Intent intent = new Intent(this, BtScannerService.class);
        intent.putExtra("command", "start");
        startService(intent);
    }

    private void stopScanning(){
        Intent intent = new Intent(this, BtScannerService.class);
        intent.putExtra("command", "stop");
        startService(intent);
    }

    private void startBLE() {
        Intent intent = new Intent(this, BtAdvertiserService.class);
        intent.putExtra("command", "start");
        startService(intent);
    }


    private void stopBLE(){
        Intent intent = new Intent(this, BtAdvertiserService.class);
        intent.putExtra("command", "stop");
        startService(intent);
    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        listener = new MyLocationListener();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(MyLocation location, MyLocation currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getEndTime() - currentBestLocation.getEndTime();
        boolean isSignificantlyNewer = timeDelta > SIGNIFICANT_TIME;
        //boolean isSignificantlyOlder = timeDelta < -SIGNIFICANT_TIME;
        //boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        }

        double diffInKM = Math.abs(getDistanceFromLatLonInKm(location.getLatitude(),location.getLongitude(),currentBestLocation.getLatitude(),currentBestLocation.getLongitude()));
        return diffInKM > 0.10;
    }

    public double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (R * c);
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        Log.v("STOP_SERVICE", "DONE");
        try {
            locationsTimer.cancel();
            advertisingTimer.cancel();
            scanningTimer.cancel();
            stopBLE();
            stopScanning();
        }catch (Exception e){}
        try {
            mlocManager.removeUpdates(listener);
        }catch (Exception e){

        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(scannerBroadcast);
        super.onDestroy();
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*****", "Location changed");

            long now = System.currentTimeMillis();
            MyLocation location = new MyLocation(loc.getLatitude(), loc.getLongitude(),loc.getProvider(),now,now,loc.getAccuracy());
            if (isBetterLocation(location, previousBestLocation)) { //Check if better location; if it is writing new line if not updating current one
                previousBestLocation = location;
                sendLocationEvent(location);

            }else{

//        // Check whether the new location fix is more or less accurate
                int accuracyDelta = (int) (location.getAccuracy() - previousBestLocation.getAccuracy());
                boolean isMoreAccurate = accuracyDelta > 0;

                // Check if the old and new location are from the same provider
//                boolean isFromSameProvider = isSameProvider(location.getProvider(),
//                        previousBestLocation.getProvider());

                //Determine location quality using a combination of timeliness and accuracy
                if (isMoreAccurate) {
                  previousBestLocation.setAccuracy(location.getAccuracy());
                  previousBestLocation.setLatitude(location.getLatitude());
                  previousBestLocation.setLongitude(location.getLongitude());
                }

                previousBestLocation.setEndTime(location.getEndTime());
                previousBestLocation.setUpdateTime(System.currentTimeMillis());
                sendLocationEvent(previousBestLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getApplicationContext(), "Status Changed", Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLocationEvent(MyLocation location) {
        Log.i("sending_location","sending...");
        new Thread(){
            public void run(){
                super.run();
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList.size() > 0){
                        Address address = addressList.get(0);
                        location.setAdminArea(address.getAdminArea());
                        location.setCountryCode(address.getCountryCode());
                        location.setFeatureName(address.getFeatureName());
                        location.setSubAdminArea(address.getSubAdminArea());
                        location.setAddressLine(address.getAddressLine(0));
                    }
                } catch (Exception e) {
                }
                boolean isNew = location.getId() == 0;
                if(isNew) {

                    long locationId = db.addLocation(location);

                    if (previousBestLocation!=null && previousBestLocation.equals(location)) {
                        previousBestLocation.setId(locationId);
                    }
                    location.setId(locationId);
                }else {
                    int success = db.updateLocation(location);
                    if(success == 0) return; //Failed to update location ?
                }

                Intent receiverIntent = new Intent(BROADCAST_CHANNEL);
                receiverIntent.putExtra("command",isNew ? "new_location" : "location_changed");
                receiverIntent.putExtra("location",location);

                LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(receiverIntent);
            }
        }.start();
    }

    // Iterates over devices and checks for devices last seen more than 5 minutes ago, in order to close the connection and log duration spent together
    private void checkIdleConnections() {
        Long currentTime = System.currentTimeMillis();

        Log.i("BLE", "Checking for idle devices");
        Log.i("BLE", "Devices in map:" + btInteractions.toString());

        for (Interaction interaction : btInteractions.values()) {
            if (currentTime - interaction.getLastSeen() >= IDLE_DURATION) { // Case device last seen longer than IDLE_DURATION

                // Here we want to remove device from the map and in case user was in contact for long enough, log contact in database
                btInteractions.remove(interaction.getUuid());
                Log.i("BLE", "Removing idle device from map: " + interaction.toString());
                if (interaction.getLastSeen() - interaction.getFirstSeen() >= CONTACT_DURATION) { // If the interaction lasted for long enough for it to be logged
                    db.addInteraction(interaction);
                }
            }
        }
    }
}
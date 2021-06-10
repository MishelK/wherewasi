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
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kdkvit.wherewasi.MainActivity;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.utils.Configs;

import actions.ServerRequestManager;
import models.Interaction;
import models.MyLocation;
import utils.DatabaseHandler;

import static com.kdkvit.wherewasi.services.BtScannerService.CONTACT_DURATION;
import static com.kdkvit.wherewasi.services.BtScannerService.IDLE_DURATION;

public class LocationService extends Service {
    public static final String BROADCAST_CHANNEL = "WhereWasI Broadcast";
    private final int SIGNIFICANT_TIME = Configs.SIGNIFICANT_TIME;
    private static final String CHANNEL_NAME = "WhereWasSI Channel";
    private final long ADVERTISING_DELAY = Configs.ADVERTISING_DELAY;
    private final long SCANNING_DELAY = Configs.SCANNING_DELAY;
    private final long SENDING_DELAY = Configs.SENDING_DELAY;
    private final long TIME_BETWEEN_CHECKING_LOCATIONS = Configs.TIME_BETWEEN_CHECKING_LOCATIONS;
    private final double KM_BETWEEN_LOCATIONS = Configs.KM_BETWEEN_LOCATIONS;

    public FusedLocationProviderClient mlocManager;
    public MyLocationListener listener;
    public MyLocation previousBestLocation = null;
    private HashMap<String, Interaction> btInteractions = new HashMap<>();

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
    private Timer interactionsTimer = new Timer();
    private BroadcastReceiver scannerBroadcast;
    private RemoteViews remoteViews;


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

        remoteViews = new RemoteViews(getPackageName(), R.layout.notif_layout);

        //        Intent closeIntent = new Intent(this, LocationService.class);
//        closeIntent.putExtra("command", "close");
//        PendingIntent closePendingIntent = PendingIntent.getService(this, 1, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.stop_service_btn, closePendingIntent);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("working", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setCustomContentView(remoteViews);

        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(R.mipmap.ic_stat_app_icon);

        startForeground(NOTIF_ID, builder.build());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            if (command != null) {
                switch (command) {
                    case "app_created":
                        initLocation();
                        initNotificationInteractions();
                        break;
                    case "close":
                        close();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initNotificationInteractions() {
        interactionsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                List<Interaction> interactions = db.getInteractionsOnDay(currentTime);
                int all = interactions.size();
                int positives = 0;
                for (Interaction interaction : interactions){
                    if(interaction.isPositive()){
                        positives++;
                    }
                }
                updateNotifView(all,positives);
            }
        },500,60*1000);
    }

    private void close() {
        stopSelf();
        Intent receiverIntent = new Intent(BROADCAST_CHANNEL);
        receiverIntent.putExtra("command", "close");
        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(receiverIntent);
    }


    public void initLocation() {
        db = new DatabaseHandler(this); // init database handler

        mlocManager = LocationServices.getFusedLocationProviderClient(this);
        listener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No premissions", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setSmallestDisplacement(50);


        mlocManager.requestLocationUpdates(locationRequest, listener, Looper.getMainLooper());

        locationsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("test", "timer test");
                if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

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
            if (btInteractions.get(interaction.getUuid()).getRssi() < -30 && btInteractions.get(interaction.getUuid()).getRssi() > -100) { // Case interaction within danger range
                btInteractions.get(interaction.getUuid()).setIsDangerous(1);
            }
            if (!interaction.isConfirmed() && interaction.getLastSeen() - interaction.getFirstSeen() >= 15000) { // If interaction is 15 minutes and going
                ServerRequestManager.sendConfirmationRequest(this, interaction.getUuid(), new ServerRequestManager.ActionsCallback() {
                    @Override
                    public void onSuccess() throws InterruptedException {
                       // Start listening and wait for response
                        Intent intent = new Intent(getApplicationContext(), SoniTalkService.class);
                        intent.putExtra("command","start_listening");
                        getApplicationContext().startService(intent);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
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
        return diffInKM >= KM_BETWEEN_LOCATIONS;
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
            mlocManager.removeLocationUpdates(listener);
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

    public class MyLocationListener extends LocationCallback {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("*****", "Location changed");
            Log.i("*****", String.valueOf(locationResult.getLastLocation().getLatitude()));
            Log.i("*****", String.valueOf(locationResult.getLastLocation().getLongitude()));
            Log.i("*****", String.valueOf(locationResult.getLocations().size()));
            Location loc = locationResult.getLastLocation();
            long now = System.currentTimeMillis();
            MyLocation location = new MyLocation(loc.getLatitude(), loc.getLongitude(),loc.getProvider(),now,now,loc.getAccuracy());
            if (isBetterLocation(location, previousBestLocation)) { //Check if better location; if it is writing new line if not updating current one
                previousBestLocation = location;
                sendLocationEvent(location);

            }else{

//        // Check whether the new location fix is more or less accurate
                int accuracyDelta = (int) (location.getAccuracy() - previousBestLocation.getAccuracy());
                boolean isMoreAccurate = accuracyDelta > 0;



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
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
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
                if (interaction.getLastSeen() - interaction.getFirstSeen() >= CONTACT_DURATION && interaction.getIsDangerous() == 1) { // If the interaction lasted for long enough for it to be logged and if it is withing danger range
                    db.addInteraction(interaction);
                }
            }
        }
    }

    private void updateNotifView(int all,int positives){
        String allText = getResources().getString(R.string.today_interactions) + ": " + String.valueOf(all);
        String positivesText = getResources().getString(R.string.today_positives) + ": " + String.valueOf(all);
        remoteViews.setTextViewText(R.id.notification_interactions_text,allText);
        remoteViews.setTextViewText(R.id.notification_positives_text,positivesText);
        if(positives > 0) {
            remoteViews.setImageViewResource(R.id.notification_status, R.drawable.circle_dra_red);
        }else if (all > 0){
            remoteViews.setImageViewResource(R.id.notification_status, R.drawable.circle_dra_orange);
        }else{
            remoteViews.setImageViewResource(R.id.notification_status, R.drawable.circle_dra_gren);
        }
        manager.notify(NOTIF_ID,builder.build());
    }
}
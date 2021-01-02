package com.kdkvit.wherewasi;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationService extends Service {
    public static final String BROADCAST_CHANNEL = "WhereWasI Broadcast";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String CHANNEL_NAME = "WhereWasSI Channel";
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    DatabaseHandler db;

    NotificationManager manager;
    NotificationCompat.Builder builder;
    String channelId = "KDKVIT_NOTIF_CHANNEL";
    final int NOTIF_ID = 1;

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
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.stop_service_btn,closePendingIntent);


        builder.setCustomContentView(remoteViews);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("working", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);

        startForeground(NOTIF_ID, builder.build());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        if(command!=null) {
            switch (command) {
                case "app_created":
                    initLocation();
//                return START_STICKY;
                case "close":
                    close();
                    break;
            }
        }

        return super.onStartCommand(intent,flags,startId);
    }

    private void close() {
        stopSelf();
        Intent receiverIntent = new Intent(BROADCAST_CHANNEL);
        receiverIntent.putExtra("close",true);
        LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(receiverIntent);
    }

    public void initLocation(){
        db = new DatabaseHandler(this); // init database handler

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
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

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
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
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");

        locationManager.removeUpdates(listener);
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
            if (isBetterLocation(loc, previousBestLocation)) {
                Intent receiverIntent = new Intent(BROADCAST_CHANNEL);
                loc.getLatitude();
                loc.getLongitude();
                MyLocation location = new MyLocation(loc.getLatitude(), loc.getLongitude(),loc.getProvider());
                receiverIntent.putExtra("location",location);
                LocalBroadcastManager.getInstance(LocationService.this).sendBroadcast(receiverIntent);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
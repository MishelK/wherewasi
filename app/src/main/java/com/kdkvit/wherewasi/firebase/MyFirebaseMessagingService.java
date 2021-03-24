package com.kdkvit.wherewasi.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kdkvit.wherewasi.MainActivity;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.services.LocationService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import models.User;
import utils.DatabaseHandler;

import static actions.actions.sendUserToBe;
import static utils.NotificationCenter.NOTIFICATIONS_RECEIVER;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final String TAG = "MyFirebaseMessagingService";
    public final static String FCM_MESSAGE_RECEIVER = "message_received";
    int notifications = 2;
    // Notification messages will arrive at system tray if app is in background and will arrive at onMessageReceived if app is in foreground
    // Data Notification which have a payload will always arrive to this service onMessageReceived

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("positives");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // ...
        Log.i("BLE", "RECEIVED POSITIVE");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // In case application is in foreground
            try {
                String uuid = remoteMessage.getData().get("user_id");
                Long markTime = Long.parseLong(remoteMessage.getData().get("mark_time"));
                Long insTime = Long.parseLong(remoteMessage.getData().get("insertion_time"));
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());

                if (db.updateInteractionsToPositive(uuid, markTime, insTime) > 0) {
                    showNotification();
                    Intent receiverIntent = new Intent(NOTIFICATIONS_RECEIVER);
                    receiverIntent.putExtra("new_notification", true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
                }

            }catch (Exception e){

            }
            // If application is not in foreground then post notification

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification
    }


    private void showNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("WWIS_ALERTS", "WWIS_ALERTS", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "WWIS_ALERTS");
        builder.setContentTitle("Notification Alert, Click Me!");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("bla", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_stat_app_icon);

        builder.setContentText(getString(R.string.you_have_been_expose));

        manager.notify(notifications,builder.build());
        notifications++;

    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "");
    }
}

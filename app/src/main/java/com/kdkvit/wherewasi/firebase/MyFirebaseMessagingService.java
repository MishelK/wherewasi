package com.kdkvit.wherewasi.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kdkvit.wherewasi.MainActivity;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.services.SoniTalkService;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.util.Timer;
import java.util.TimerTask;

import Managers.InteractionListManager;
import models.User;
import utils.DatabaseHandler;

import static Managers.ServerRequestManager.sendUserToBe;
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
        SharedPreferences sp = getSharedPreferences("_", MODE_PRIVATE);
        sp.edit().putString("fb", s).apply();
        User user = SharedPreferencesUtils.getUser(this);
        if (user != null) {
            user.setFcmId(s);
            sendUserToBe(this, user);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // ...
        Log.i("BLE", "RECEIVED POSITIVE");
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("type")) {
            // In case application is in foreground
            switch (remoteMessage.getData().get("type")){
                case "positive_detected":
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
                    break;
                case "start_listening":
                    if (!SoniTalkService.isBusy()) {
                        if (remoteMessage.getData().containsKey("user_id"))
                            InteractionListManager.getInstance(getApplicationContext()).setUuidUnderTest(remoteMessage.getData().get("user_id"));
                        Intent intent = new Intent(getApplicationContext(), SoniTalkService.class);
                        intent.putExtra("command", "start_listening");
                        getApplicationContext().startService(intent);

                        Timer timer = new Timer();
                        TimerTask delayedThreadStartTask = new TimerTask() { // Timer to stop listening after 60 seconds
                            @Override
                            public void run() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), SoniTalkService.class);
                                        intent.putExtra("command", "stop_listening");
                                        getApplicationContext().startService(intent);
                                    }
                                }).start();
                            }
                        };
                        timer.schedule(delayedThreadStartTask, 60 * 1000); // 1 minute
                    }
                    break;
                case "sound_received":
                    if (remoteMessage.getData().containsKey("user_id"))
                        InteractionListManager.getInstance(getApplicationContext()).setInteractionSameSpace(remoteMessage.getData().get("user_id"), true);
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

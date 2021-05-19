package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SoniTalkService extends Service {
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
                    //startScan();
                    break;
                case "stop":
                    //stopScan();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



}

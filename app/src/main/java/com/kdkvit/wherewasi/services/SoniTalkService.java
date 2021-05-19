package com.kdkvit.wherewasi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.kdkvit.wherewasi.sonitalk.SoniTalkConfig;
import com.kdkvit.wherewasi.sonitalk.SoniTalkContext;
import com.kdkvit.wherewasi.sonitalk.SoniTalkEncoder;
import com.kdkvit.wherewasi.sonitalk.SoniTalkMessage;

import java.nio.charset.StandardCharsets;

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

    private SoniTalkMessage generateMessage(SoniTalkContext soniTalkContext, SoniTalkConfig soniTalkConfig, String string) {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        SoniTalkEncoder soniTalkEncoder = soniTalkContext.getEncoder(soniTalkConfig);
        SoniTalkMessage soniTalkMessage = soniTalkEncoder.generateMessage(bytes);
        return  soniTalkMessage;
    }

}

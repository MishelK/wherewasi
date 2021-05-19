package com.kdkvit.wherewasi.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.sonitalk.SoniTalkContext;
import com.kdkvit.wherewasi.sonitalk.SoniTalkMessage;
import com.kdkvit.wherewasi.sonitalk.SoniTalkPermissionsResultReceiver;
import com.kdkvit.wherewasi.sonitalk.SoniTalkSender;
import com.kdkvit.wherewasi.sonitalk.utils.ConfigConstants;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

public class SoniTalkService extends Service implements SoniTalkPermissionsResultReceiver.Receiver {
    private SoniTalkContext soniTalkContext;
    private SoniTalkSender soniTalkSender;
    private SoniTalkPermissionsResultReceiver soniTalkPermissionsResultReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Toast currentToast;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 42;
    private String [] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

    // These request codes are used to know which action was accepted / denied by the user.
    public static final int ON_SENDING_REQUEST_CODE = 2001;
    public static final int ON_RECEIVING_REQUEST_CODE = 2002;
    private SharedPreferences sp;
    private SoniTalkMessage currentMessage;

    @Override
    public void onCreate() { //Called once when service is instantiated
        super.onCreate();

        soniTalkPermissionsResultReceiver = new SoniTalkPermissionsResultReceiver(new Handler());
        soniTalkPermissionsResultReceiver.setReceiver(this); // To receive callbacks from the SDK
        soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
        if (soniTalkContext == null) {
            soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //Called from startService()
        String command = intent.getStringExtra("command");
        if (command != null) {
            switch (command) {
                case "start":
                    startSending();
                    break;
                case "stop":
                    //stopScan();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startSending(){

    }


    public void sendMessage(){
        //Log.d("PlayClick","I got clicked");

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(3);
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt(ConfigConstants.CURRENT_VOLUME, currentVolume);
            ed.apply();

            int volume = Integer.valueOf(sp.getString(ConfigConstants.LOUDNESS, ConfigConstants.SETTING_LOUDNESS_DEFAULT));
            audioManager.setStreamVolume(3, (int) Math.round((audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume/100.0D)), 0);

            if (soniTalkContext == null) {
                soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
            }
            soniTalkSender = soniTalkContext.getSender();
            soniTalkSender.send(currentMessage, ON_SENDING_REQUEST_CODE);

//        } else{
//            Toast.makeText(getApplicationContext(), getString(R.string.signal_generator_not_created),Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onSoniTalkPermissionResult(int resultCode, Bundle resultData) {
        int actionCode = 0;
        if(resultData != null){
            actionCode = resultData.getInt(getString(R.string.bundleRequestCode_key));
        }
        switch (resultCode) {
            case SoniTalkContext.ON_PERMISSION_LEVEL_DECLINED:
                //Log.d(TAG, "onSoniTalkPermissionResult ON_PERMISSION_LEVEL_DECLINED");
                //Log.d(TAG, String.valueOf(resultData.getInt(getString(R.string.bundleRequestCode_key), 0)));

                if (currentToast != null) {
                    currentToast.cancel();
                }
                switch (actionCode) {
                    case ON_RECEIVING_REQUEST_CODE:
                        currentToast = Toast.makeText(this, getString(R.string.on_receiving_listening_permission_required), Toast.LENGTH_SHORT);
                        currentToast.show();
                        //onButtonStopListening();

                        // Set buttons in the state NOT RECEIVING

                        break;
                    case ON_SENDING_REQUEST_CODE:
                        currentToast = Toast.makeText(this, getString(R.string.on_sending_sending_permission_required), Toast.LENGTH_SHORT);
                        currentToast.show();

                        // Set buttons in the state NOT RECEIVING

                        break;
                }


                break;
            case SoniTalkContext.ON_REQUEST_GRANTED:
                //Log.d(TAG, "ON_REQUEST_GRANTED");
                //Log.d(TAG, String.valueOf(resultData.getInt(getString(R.string.bundleRequestCode_key), 0)));

                switch (actionCode){
                    case ON_RECEIVING_REQUEST_CODE:
                        // Set buttons in the state RECEIVING

                        //setReceivedText(getString(R.string.decoder_start_text));
                        break;

                    case ON_SENDING_REQUEST_CODE:

                        //saveButtonStates();
                        break;
                }

                break;
            case SoniTalkContext.ON_REQUEST_DENIED:
                //Log.d(TAG, "ON_REQUEST_DENIED");
                //Log.d(TAG, String.valueOf(resultData.getInt(getString(R.string.bundleRequestCode_key), 0)));

                if (currentToast != null) {
                    currentToast.cancel();
                }

                // Checks the requestCode to adapt the UI depending on the action type (receiving or sending)
                switch (actionCode) {
                    case ON_RECEIVING_REQUEST_CODE:
                        //showRequestPermissionExplanation(R.string.on_receiving_listening_permission_required);
                        // Set buttons in the state NOT RECEIVING
                        currentToast = Toast.makeText(this, getString(R.string.on_receiving_listening_permission_required), Toast.LENGTH_SHORT);
                        currentToast.show();
                        //saveButtonState(ConfigConstants.LISTEN_BUTTON_ENABLED,true);
                        //btnListen.setImageResource(R.drawable.baseline_hearing_grey_48);

                        break;
                    case ON_SENDING_REQUEST_CODE:
                        //showRequestPermissionExplanation(R.string.on_sending_sending_permission_required);
                        currentToast = Toast.makeText(this, getString(R.string.on_sending_sending_permission_required), Toast.LENGTH_SHORT);
                        currentToast.show();
                        //saveButtonState(ConfigConstants.SEND_BUTTON_ENABLED,true);
                        //btnPlay.setImageResource(R.drawable.ic_volume_up_grey_48dp);

                }
                break;

            case SoniTalkContext.ON_SEND_JOB_FINISHED:
                ///saveButtonState(ConfigConstants.SEND_BUTTON_ENABLED,true);
                //btnPlay.setImageResource(R.drawable.ic_volume_up_grey_48dp);
                sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int currentVolume = sp.getInt(ConfigConstants.CURRENT_VOLUME, ConfigConstants.CURRENT_VOLUME_DEFAULT);
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(3, currentVolume, 0);

                break;

            case SoniTalkContext.ON_SHOULD_SHOW_RATIONALE_FOR_ALLOW_ALWAYS:
                /*if (currentToast != null) {
                    currentToast.cancel();
                }
                currentToast = Toast.makeText(MainActivity.this, "Choosing Allow always requires you to accept the Android permission", Toast.LENGTH_LONG);
                currentToast.show();*/
                break;

            case SoniTalkContext.ON_REQUEST_L0_DENIED:
                //Log.d(TAG, "ON_REQUEST_L0_DENIED");
                switch (actionCode) {
                    case ON_RECEIVING_REQUEST_CODE:
                        showRequestPermissionExplanation(R.string.on_receiving_listening_permission_required);

                        break;
                    case ON_SENDING_REQUEST_CODE:
                        showRequestPermissionExplanation(R.string.on_sending_sending_permission_required);


                }
                break;

            default:

                break;

        }
    }

    private void showRequestPermissionExplanation(int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.permission_request_explanation_positive,new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
        );
        builder.setNegativeButton(R.string.permission_request_explanation_negative, null);
        builder.show();
    }
}

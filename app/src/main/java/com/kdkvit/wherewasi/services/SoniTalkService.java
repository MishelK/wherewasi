package com.kdkvit.wherewasi.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.sonitalk.SoniTalkConfig;
import com.kdkvit.wherewasi.sonitalk.SoniTalkContext;
import com.kdkvit.wherewasi.sonitalk.SoniTalkDecoder;
import com.kdkvit.wherewasi.sonitalk.SoniTalkEncoder;
import com.kdkvit.wherewasi.sonitalk.SoniTalkMessage;
import com.kdkvit.wherewasi.sonitalk.SoniTalkPermissionsResultReceiver;
import com.kdkvit.wherewasi.sonitalk.SoniTalkSender;
import com.kdkvit.wherewasi.sonitalk.exceptions.ConfigException;
import com.kdkvit.wherewasi.sonitalk.exceptions.DecoderStateException;
import com.kdkvit.wherewasi.sonitalk.utils.ConfigConstants;
import com.kdkvit.wherewasi.sonitalk.utils.ConfigFactory;
import com.kdkvit.wherewasi.sonitalk.utils.DecoderUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

public class SoniTalkService extends Service implements SoniTalkPermissionsResultReceiver.Receiver,  SoniTalkDecoder.MessageListener {
    private static final String TAG = "SoniTalkService";
    public static final String SONITALK_RECEIVER = "SoniTalkReceiver";

    private SoniTalkContext soniTalkContext;
    private SoniTalkSender soniTalkSender;
    private SoniTalkPermissionsResultReceiver soniTalkPermissionsResultReceiver;
    SoniTalkConfig config;
    private SoniTalkEncoder soniTalkEncoder;
    private SoniTalkDecoder soniTalkDecoder;
    private int samplingRate = 44100;
    private int fftResolution = 4410;
    private static boolean isListening, isSending = false;

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
        try {
            //config = ConfigFactory.getDefaultConfig(this);
            config = ConfigFactory.loadFromJson("hearable4000.json",this);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }
    private AudioTrack playerFrequency;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(NUMBER_OF_CORES + 1);

    public void generateMessage(){
        if(playerFrequency!=null){
            playerFrequency.stop();
            playerFrequency.flush();
            playerFrequency.release();
            playerFrequency = null;
            //Log.d("Releaseaudio","playerFrequency releaese");
        }
        //Log.d("GenerateClick","I got clicked");

        isSending = true;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int bitperiod = Integer.valueOf(sp.getString(ConfigConstants.BIT_PERIOD, ConfigConstants.SETTING_BIT_PERIOD_DEFAULT));
        int pauseperiod = Integer.valueOf(sp.getString(ConfigConstants.PAUSE_PERIOD, ConfigConstants.SETTING_PAUSE_PERIOD_DEFAULT));
        int f0 = Integer.valueOf(sp.getString(ConfigConstants.FREQUENCY_ZERO, ConfigConstants.SETTING_FREQUENCY_ZERO_DEFAULT));
        int nFrequencies = Integer.valueOf(sp.getString(ConfigConstants.NUMBER_OF_FREQUENCIES, ConfigConstants.SETTING_NUMBER_OF_FREQUENCIES_DEFAULT));
        int frequencySpace = Integer.valueOf(sp.getString(ConfigConstants.SPACE_BETWEEN_FREQUENCIES, ConfigConstants.SETTING_SPACE_BETWEEN_FREQUENCIES_DEFAULT));
        int nMaxBytes = Integer.valueOf(sp.getString(ConfigConstants.NUMBER_OF_BYTES, ConfigConstants.SETTING_NUMBER_OF_BYTES_DEFAULT));

        int nMessageBlocks = (nMaxBytes+2) / 2; // We want 10 message blocks by default
        SoniTalkConfig config = new SoniTalkConfig(f0, bitperiod, pauseperiod, nMessageBlocks, nFrequencies, frequencySpace);
        if (soniTalkContext == null) {
            soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
        }
        soniTalkEncoder = soniTalkContext.getEncoder(config);

        //final String textToSend = sp.getString(ConfigConstants.SIGNAL_TEXT,"Hallo Sonitalk");
        final String textToSend = "hello";
        //Log.d("changeToBitStringUTF8",String.valueOf(isUTF8MisInterpreted(textToSend, "Windows-1252")));

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sp.contains(ConfigConstants.TEXT_TO_SEND)){
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(ConfigConstants.TEXT_TO_SEND, textToSend);
            ed.apply();
        }

        if(sp.getString(ConfigConstants.TEXT_TO_SEND,"Hallo SoniTalk").equals(textToSend) && currentMessage != null){
            sendMessage();
        }else {
            SharedPreferences.Editor ed = sp.edit();
            ed.putString(ConfigConstants.TEXT_TO_SEND, textToSend);
            ed.apply();

            final byte[] bytes = textToSend.getBytes(StandardCharsets.UTF_8);


            // Move the background execution handling away from the Activity (in Encoder or Service or AsyncTask). Creating Runnables here may leak the Activity
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    currentMessage = soniTalkEncoder.generateMessage(bytes);
                    sendMessage();
                    isSending = false;
                }
            });

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //Called from startService()
        if (intent != null && intent.hasExtra("command")) {
            String command = intent.getStringExtra("command");

            if (command != null) {
                switch (command) {
                    case "start_playing":
                        if (!isSending) {
                            startSending();
                        }
                        break;
                    case "stop_playing":
                        //stopScan();
                        break;
                    case "start_listening":
                        startDecoder();
                        break;
                    case "stop_listening":
                        if (isListening)
                            stopDecoder();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startSending(){
        generateMessage();
    }


    public void sendMessage() {
        //Log.d("PlayClick","I got clicked");

        if (currentMessage != null) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(3);
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt(ConfigConstants.CURRENT_VOLUME, currentVolume);
            ed.apply();

            int volume = Integer.valueOf(sp.getString(ConfigConstants.LOUDNESS, ConfigConstants.SETTING_LOUDNESS_DEFAULT));
            audioManager.setStreamVolume(3, (int) Math.round((audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume / 100.0D)), 0);

            if (soniTalkContext == null) {
                soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
            }
            soniTalkSender = soniTalkContext.getSender();
            soniTalkSender.send(currentMessage, ON_SENDING_REQUEST_CODE);

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.signal_generator_not_created), Toast.LENGTH_LONG).show();
        }
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

    private SoniTalkMessage generateMessage(SoniTalkContext soniTalkContext, SoniTalkConfig soniTalkConfig, String string) {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        SoniTalkEncoder soniTalkEncoder = soniTalkContext.getEncoder(soniTalkConfig);
        SoniTalkMessage soniTalkMessage = soniTalkEncoder.generateMessage(bytes);
        return  soniTalkMessage;
    }

    private void startDecoder() {
        int frequencyOffsetForSpectrogram = 50;
        int stepFactor = 8;
        isListening = true;

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int bitperiod = Integer.valueOf(sp.getString(ConfigConstants.BIT_PERIOD, ConfigConstants.SETTING_BIT_PERIOD_DEFAULT));
        int pauseperiod = Integer.valueOf(sp.getString(ConfigConstants.PAUSE_PERIOD, ConfigConstants.SETTING_PAUSE_PERIOD_DEFAULT));
        int f0 = Integer.valueOf(sp.getString(ConfigConstants.FREQUENCY_ZERO, ConfigConstants.SETTING_FREQUENCY_ZERO_DEFAULT));
        int nFrequencies = Integer.valueOf(sp.getString(ConfigConstants.NUMBER_OF_FREQUENCIES, ConfigConstants.SETTING_NUMBER_OF_BYTES_DEFAULT));
        int frequencySpace = Integer.valueOf(sp.getString(ConfigConstants.SPACE_BETWEEN_FREQUENCIES, ConfigConstants.SETTING_SPACE_BETWEEN_FREQUENCIES_DEFAULT));
        int nMaxBytes = Integer.valueOf(sp.getString(ConfigConstants.NUMBER_OF_BYTES, ConfigConstants.SETTING_NUMBER_OF_BYTES_DEFAULT));

        try {
            SoniTalkConfig config = ConfigFactory.getDefaultConfig(this.getApplicationContext());
            // Note: here for debugging purpose we allow to change almost all the settings of the protocol.
            config.setFrequencyZero(f0);
            config.setBitperiod(bitperiod);
            config.setPauseperiod(pauseperiod);
            int nMessageBlocks = (nMaxBytes+2) / 2; // Default is 10 (transmitting 20 bytes with 16 frequencies)
            config.setnMessageBlocks(nMessageBlocks);
            config.setnFrequencies(nFrequencies);
            config.setFrequencySpace(frequencySpace);

            // Testing usage of a config file placed in the final-app asset folder.
            // SoniTalkConfig config = ConfigFactory.loadFromJson("lowFrequenciesConfig.json", this.getApplicationContext());

            if (soniTalkContext == null) {
                soniTalkContext = SoniTalkContext.getInstance(this, soniTalkPermissionsResultReceiver);
            }
            soniTalkDecoder = soniTalkContext.getDecoder(samplingRate, config); //, stepFactor, frequencyOffsetForSpectrogram, silentMode);
            soniTalkDecoder.addMessageListener(this); // MainActivity will be notified of messages received (calls onMessageReceived)
            //soniTalkDecoder.addSpectrumListener(this); // Can be used to receive the spectrum when a message is decoded.

            // Should not throw the DecoderStateException as we just initialized the Decoder
            soniTalkDecoder.receiveBackground(ON_RECEIVING_REQUEST_CODE);

        } catch (DecoderStateException e) {
            setReceivedText("getString(R.string.decoder_exception_state)" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "getString(R.string.decoder_exception_io)" + e.getMessage());
        } catch (ConfigException e) {
            Log.e(TAG, "getString(R.string.decoder_exception_config)" + e.getMessage());
        }

    }

    public void onButtonStopListening(){
        stopDecoder();
        setReceivedText("");
    }

    private void stopDecoder() {

        isListening = false;
        if (soniTalkDecoder != null) {
            soniTalkDecoder.stopReceiving();
        }
        soniTalkDecoder = null;
        Intent receiverIntent = new Intent(SONITALK_RECEIVER);
        receiverIntent.putExtra("command","stop_listening");
        LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
    }

    public void setReceivedText(String decodedText){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(SoniTalkService.this.getApplicationContext(), decodedText, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onMessageReceived(SoniTalkMessage receivedMessage) {

        if (receivedMessage.isCrcCorrect()) {
            //Log.d("ParityCheck", "The message was correctly received");
            final String textReceived = DecoderUtils.byteToUTF8(receivedMessage.getMessage());
            //Log.d("Received message", textReceived);

            // We stop when CRC is correct and we are not in silent mode

            // Update text displayed
            setReceivedText(textReceived + " (" + String.valueOf(receivedMessage.getDecodingTimeNanosecond() / 1000000) + "ms)");

            if (currentToast != null) {
                currentToast.cancel(); // NOTE: Cancel so fast that only the last one in a series really is displayed.
            }
            // Stops recording if needed and shows a Toast
            if (false) {
                // STOP everything.
                stopDecoder();
//                        currentToast = Toast.makeText(MainActivity.this, "Correctly received a message. Stopped.", Toast.LENGTH_SHORT);
//                        currentToast.show();
            } else {
//                        currentToast = Toast.makeText(MainActivity.this, "Correctly received a message. Keep listening.", Toast.LENGTH_SHORT);
//                        currentToast.show();
            }
        } else {
            //Log.d("ParityCheck", "The message was NOT correctly received");

            //main.setReceivedText("Please try again, could not detect or decode the message!");

            if (currentToast != null) {
                currentToast.cancel(); // NOTE: Cancel so fast that only the last one in a series really is displayed.
            }
            if (true) {
                setReceivedText("Message received");
                stopDecoder();
                //setReceivedText("getString(R.string.detection_crc_incorrect)");
//                        currentToast = Toast.makeText(MainActivity.this, getString(R.string.detection_crc_incorrect_toast_message), Toast.LENGTH_LONG);
//                        currentToast.show();
            } else {
                setReceivedText("getString(R.string.detection_crc_incorrect_keep_listening)");
//                        currentToast = Toast.makeText(MainActivity.this, getString(R.string.detection_crc_incorrect_keep_listening_toast_message), Toast.LENGTH_LONG);
//                        currentToast.show();
            }
        }

    }

    @Override
    public void onDecoderError(String errorMessage) {
        //Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        // STOP everything.
        stopDecoder();
        setReceivedText(errorMessage);
    }

    public static boolean isBusy() {
        return isListening || isSending;
    }
}

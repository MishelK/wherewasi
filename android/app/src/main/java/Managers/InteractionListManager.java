package Managers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kdkvit.wherewasi.services.SoniTalkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import models.Interaction;
import utils.DatabaseHandler;

import static com.kdkvit.wherewasi.services.BtScannerService.CONTACT_DURATION;
import static com.kdkvit.wherewasi.services.BtScannerService.IDLE_DURATION;

/**
 * Manages the interaction list operations, adding, saving to db, and interaction logic including space confirmations
 */
public class InteractionListManager {

    private HashMap<String, Interaction> btInteractions;
    private static InteractionListManager interactionListManager;
    private Context context;
    private String uuidUnderTest;

    protected InteractionListManager(Context context) {
        this.btInteractions = new HashMap<>();
        this.context = context;
    }

    public static InteractionListManager getInstance(Context context) {
        if (interactionListManager == null)
            interactionListManager = new InteractionListManager(context);
        return interactionListManager;
    }


    public HashMap<String, Interaction> getInteractionList() {
        return btInteractions;
    }

    public void setInteractionList(HashMap<String, Interaction> interactionList) {
        this.btInteractions = interactionList;
    }

    /**
     * Called when a new interaction list is provided, if an interaction is new, it is added to the list, if it already exists, the last seen timestamp is updated
     */
    public void addNewInteractions(ArrayList<Interaction> interactions) {

        for(Interaction interaction : interactions){

            if(btInteractions.containsKey(interaction.getUuid())){
                btInteractions.get(interaction.getUuid()).setLastSeen(System.currentTimeMillis());
            }else{
                btInteractions.put(interaction.getUuid(),interaction);
            }
            if (btInteractions.get(interaction.getUuid()).getRssi() < -30 && btInteractions.get(interaction.getUuid()).getRssi() > -100) { // Case interaction within danger range
                btInteractions.get(interaction.getUuid()).setIsDangerous(true);
            }
            if (!SoniTalkService.isBusy() && !btInteractions.get(interaction.getUuid()).isConfirmedSameSpace() && (System.currentTimeMillis() - btInteractions.get(interaction.getUuid()).getLastConfirmationRequest()) > 60000
                    && btInteractions.get(interaction.getUuid()).getLastSeen() - btInteractions.get(interaction.getUuid()).getFirstSeen() >= 15000) { // If interaction is 15 minutes and going

                ServerRequestManager.sendConfirmationRequest(context, interaction.getUuid(), new ServerRequestManager.ActionsCallback() {
                    @Override
                    public void onSuccess() throws InterruptedException {
                        // Start listening and wait for response
                        if (!SoniTalkService.isBusy()) {
                            Timer timer = new Timer();
                            TimerTask delayedThreadStartTask = new TimerTask() {
                                @Override
                                public void run() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(context, SoniTalkService.class);
                                            intent.putExtra("command", "start_playing");
                                            context.startService(intent);
                                        }
                                    }).start();
                                }
                            };
                            timer.schedule(delayedThreadStartTask, 10 * 1000); //1 minute
                        }
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            }
        }
    }

    /**
     * Marks and interaction as confirmed in the same space
     */
    public void setInteractionSameSpace(String uuid, boolean isSameSpace) {
        if (btInteractions.containsKey(uuid))
            btInteractions.get(uuid).setConfirmedSameSpace(isSameSpace);
    }

    public void setUuidUnderTest(String uuidUnderTest) {
        this.uuidUnderTest = uuidUnderTest;
    }

    public String getUuidUnderTest() {
        return uuidUnderTest;
    }

    /**
     * Checks for connections that have not been seen for x time and saves them if so.
     */
    // Iterates over devices and checks for devices last seen more than 5 minutes ago, in order to close the connection and log duration spent together
    public void checkIdleConnections() {
        DatabaseHandler db = new DatabaseHandler(context);
        Long currentTime = System.currentTimeMillis();

        Log.i("BLE", "Checking for idle devices");
        Log.i("BLE", "Devices in map:" + btInteractions.toString());

        for (Interaction interaction : btInteractions.values()) {
            if (currentTime - interaction.getLastSeen() >= IDLE_DURATION) { // Case device last seen longer than IDLE_DURATION

                // Here we want to remove device from the map and in case user was in contact for long enough, log contact in database
                btInteractions.remove(interaction.getUuid());
                Log.i("BLE", "Removing idle device from map: " + interaction.toString());
                if (interaction.getLastSeen() - interaction.getFirstSeen() >= CONTACT_DURATION && interaction.isDangerous()) { // If the interaction lasted for long enough for it to be logged and if it is withing danger range
                    db.addInteraction(interaction);
                }
            }
        }
        db.close();
    }


}

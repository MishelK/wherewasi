package utils;

import android.content.Context;

import com.kdkvit.wherewasi.R;

import java.util.ArrayList;
import java.util.List;

import models.MyNotification;

public class NotificationCenter {

    public static final long MILLIS_IN_DAY = 86400000;

    public static final String NOTIFICATIONS_RECEIVER = "WWI_NOTIFICATIONS_RECEIVER";

    public static List<MyNotification> generateDailyNotifications(Context context) {
        List<MyNotification> notifList = new ArrayList<>();

        DatabaseHandler db = new DatabaseHandler(context);
//
//        // Number of interactions today
//        int numOfInteractionsToday = db.getNumOfInteractionsOnDay(System.currentTimeMillis(),true);
//        if (numOfInteractionsToday > 0) {
//            String text = context.getString(R.string.positive_founds);
//            text = String.format(text, String.valueOf(numOfInteractionsToday));
//            notifList.add(new MyNotification(0, text));
//        }


        // Highest number of interactions past 7 days
        int maxInteractions7Day = 0;
        for (int i = 0; i < 14 ; i++){ // finding max
            long currentTime = System.currentTimeMillis();
            long dateInMillis = currentTime - (i * MILLIS_IN_DAY);
            int num = db.getNumOfInteractionsOnDay(dateInMillis,true);
            if(num > 0) {
                String text = context.getString(R.string.positive_founds);
                text = String.format(text, String.valueOf(num));
                notifList.add(new MyNotification(i, text));
            }
        }
//        if (maxInteractions7Day == 0)
//            stringList.add("No interactions on record past week");
//        else
//            stringList.add("Last 7 days interaction high: " + maxInteractions7Day);
//
//        // Positive interactions past week
//        int numOfPositiveInteractionsWeek = db.getNumOfPositivesBetweenDates(System.currentTimeMillis() - 604800000, System.currentTimeMillis());
//        if (numOfPositiveInteractionsWeek == 0)
//            stringList.add("There were no 'Positive' interactions past week");
//        else
//            stringList.add("Number of 'Positive' interaction past week: " + numOfPositiveInteractionsWeek);
//
//        // Positive interactions so far
//        int numOfPositiveSoFar = db.getNumOfPositivesSoFar();
//        if (numOfPositiveSoFar == 0)
//            stringList.add("You have no 'Positive' interactions on record");
//        else
//            stringList.add("So far, you had " + numOfPositiveSoFar + " 'Positive' interactions");
//
//        return stringList;
        return notifList;
    }

}

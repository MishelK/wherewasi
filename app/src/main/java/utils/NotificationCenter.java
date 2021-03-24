package utils;

import android.content.Context;

import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationCenter {

    public static final long MILLIS_IN_DAY = 86400000;

    public NotificationCenter() {
    }

    public List<String> generateDailyNotifications(Context context) {
        List<String> stringList = new ArrayList<>();

        DatabaseHandler db = new DatabaseHandler(context);

        // Number of interactions today
        int numOfInteractionsToday = db.getNumOfInteractionsOnDay(System.currentTimeMillis());
        if (numOfInteractionsToday == 0)
            stringList.add("No interactions on record from today");
        else
            stringList.add("Today's interactions so far: " + numOfInteractionsToday);


        // Highest number of interactions past 7 days
        int maxInteractions7Day = 0;
        for (int i = 6; i>=0; i--){ // finding max
            long currentTime = System.currentTimeMillis();
            long dateInMillis = currentTime - (i * MILLIS_IN_DAY);
            int num = db.getNumOfInteractionsOnDay(dateInMillis);

            if (num > maxInteractions7Day) maxInteractions7Day = num;
        }
        if (maxInteractions7Day == 0)
            stringList.add("No interactions on record past week");
        else
            stringList.add("Last 7 days interaction high: " + maxInteractions7Day);

        // Positive interactions past week
        int numOfPositiveInteractionsWeek = db.getNumOfPositivesBetweenDates(System.currentTimeMillis() - 604800000, System.currentTimeMillis());
        if (numOfPositiveInteractionsWeek == 0)
            stringList.add("There were no 'Positive' interactions past week");
        else
            stringList.add("Number of 'Positive' interaction past week: " + numOfPositiveInteractionsWeek);

        // Positive interactions so far
        int numOfPositiveSoFar = db.getNumOfPositivesSoFar();
        if (numOfPositiveSoFar == 0)
            stringList.add("You have no 'Positive' interactions on record");
        else
            stringList.add("So far, you had " + numOfPositiveSoFar + " 'Positive' interactions");

        return stringList;
    }

}

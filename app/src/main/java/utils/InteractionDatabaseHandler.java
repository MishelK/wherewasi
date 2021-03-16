package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import enums.InteractionsColumn;
import models.Interaction;

public class InteractionDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wherewasi.db";
    private static final String TABLE_INTERACTIONS = "interactions";


    public InteractionDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INTERACTIONS_TABLE = "CREATE TABLE " + TABLE_INTERACTIONS + "("
                + InteractionsColumn.INTERACTION_ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InteractionsColumn.DEVICE_ID.toString() + " TEXT,"
                + InteractionsColumn.FIRST_SEEN.toString() + " INTEGER,"
                + InteractionsColumn.LAST_SEEN.toString() + " INTEGER "
                + ")";
        db.execSQL(CREATE_INTERACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERACTIONS);
        onCreate(db);
    }

    public void addInteraction(Interaction interaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InteractionsColumn.DEVICE_ID.toString(), interaction.getUuid());
        values.put(InteractionsColumn.FIRST_SEEN.toString(), interaction.getFirstSeen());
        values.put(InteractionsColumn.LAST_SEEN.toString(), interaction.getLastSeen());

        db.insert(TABLE_INTERACTIONS, null, values);
        db.close();

        Log.i("BLE","Interaction with device : " + interaction.getUuid() + " Added to DB");
    }

    public List<Interaction> getAllInteractions() {
        List<Interaction> interactions = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_INTERACTIONS, null); // Gets all interactions from table

        if(result.getCount() > 0) { // Checking if there are any interactions returned from the database
            while (result.moveToNext()) { // will be false when we ran out of unchecked results

                Interaction interaction = new Interaction();
                interaction.setInteractionID(result.getInt(0));
                interaction.setUuid(result.getString(1));
                interaction.setFirstSeen(result.getLong(2));
                interaction.setLastSeen(result.getLong(3));

                interactions.add(interaction);
            }
        }
        return interactions;
    }

    public List<Interaction> getInteractionsOnDay(long timeInMillis) { // Returns all interactions that occurred on the day of given timestamp
        List<Interaction> interactions = new ArrayList<>();

        Long dayStart = timeInMillis - timeInMillis % 86400000; // the remainder of the modulus will be time of day (time since day started)
        Long dayEnd = dayStart + 86400000;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_INTERACTIONS + " WHERE " + InteractionsColumn.FIRST_SEEN.toString() + " > ? AND " + InteractionsColumn.FIRST_SEEN.toString()
                + " < ?", new String[]{dayStart.toString(), dayEnd.toString()}); // Gets all interactions between dates from table

        if(result.getCount() > 0) { // Checking if there are any interactions returned from the database
            while (result.moveToNext()) { // will be false when we ran out of unchecked results

                Interaction interaction = new Interaction();
                interaction.setInteractionID(result.getInt(0));
                interaction.setUuid(result.getString(1));
                interaction.setFirstSeen(result.getLong(2));
                interaction.setLastSeen(result.getLong(3));

                interactions.add(interaction);
            }
        }
        return interactions;
    }

    public int getNumOfInteractionsOnDay(long timeInMillis) { // Returns all interactions that occurred on the day of given timestamp

        Long dayStart = timeInMillis - timeInMillis % 86400000; // the remainder of the modulus will be time of day (time since day started)
        Long dayEnd = dayStart + 86400000;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_INTERACTIONS + " WHERE " + InteractionsColumn.FIRST_SEEN.toString() + " > ? AND " + InteractionsColumn.FIRST_SEEN.toString()
                + " < ?", new String[]{dayStart.toString(), dayEnd.toString()}); // Gets all interactions between dates from table


        return result.getCount();
    }

    public List<Interaction> getInteractionsBetweenDates(Long from, Long to) { // Returns all interactions that occurred between given timestamps
        List<Interaction> interactions = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_INTERACTIONS + " WHERE " + InteractionsColumn.FIRST_SEEN.toString() + " > ? AND " + InteractionsColumn.FIRST_SEEN.toString() + " < ?"
                , new String[]{from.toString(), to.toString()});

        if(result.getCount() > 0) { // Checking if there are any interactions returned from the database
            while (result.moveToNext()) { // will be false when we ran out of unchecked results

                Interaction interaction = new Interaction();
                interaction.setInteractionID(result.getInt(0));
                interaction.setUuid(result.getString(1));
                interaction.setFirstSeen(result.getLong(2));
                interaction.setLastSeen(result.getLong(3));

                interactions.add(interaction);
            }
        }
        return interactions;
    }

}

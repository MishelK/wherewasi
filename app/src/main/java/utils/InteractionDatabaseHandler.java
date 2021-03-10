package utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import enums.InteractionsColumn;
import enums.LocationColumn;
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
                + InteractionsColumn.FIRST_SEEN.toString() + " TEXT,"
                + InteractionsColumn.LAST_SEEN.toString() + " TEXT "
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
        values.put(InteractionsColumn.FIRST_SEEN.toString(), interaction.getFirstSeen().toString());
        values.put(InteractionsColumn.LAST_SEEN.toString(), interaction.getLastSeen().toString());

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
                interaction.setFirstSeen(Long.parseLong(result.getString(2)));
                interaction.setLastSeen(Long.parseLong(result.getString(3)));

                interactions.add(interaction);
            }
        }
        return interactions;
    }

}

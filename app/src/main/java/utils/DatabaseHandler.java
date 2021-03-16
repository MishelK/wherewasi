package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import enums.InteractionsColumn;
import enums.LocationColumn;
import models.Interaction;
import models.LocationsGroup;
import models.MyLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kdkvit.wherewasi.utils.General.getLocationsGroup;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wherewasi.db";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_INTERACTIONS = "interactions";



    public enum SORTING_PARAM{
        LastUpdated( " order by " + LocationColumn.UPDATED_TIME.toString() + " desc"),
        firstUpdate( " order by " + LocationColumn.UPDATED_TIME.toString() + " asc"),
        firstStart(" order by " + LocationColumn.START_TIME.toString() + " asc");

        private String sorting;
        SORTING_PARAM(String s) {
            sorting = s;
        }

        public String getSorting(){
            return sorting;
        }
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + LocationColumn.ID.toString() + " INTEGER PRIMARY KEY,"
                + LocationColumn.LATITUDE.toString() + " DOUBLE,"
                + LocationColumn.LONGITUDE.toString() + " DOUBLE,"
                + LocationColumn.PROVIDER.toString() + " TEXT,"
                + LocationColumn.ADDRESS_LINE.toString() + " TEXT,"
                + LocationColumn.COUNTRY_CODE.toString() + " TEXT,"
                + LocationColumn.ADMIN_AREA.toString() + " TEXT,"
                + LocationColumn.FEATURE_NAME.toString() + " TEXT,"
                + LocationColumn.SUB_AREA_NAME.toString() + " TEXT,"
                + LocationColumn.START_TIME.toString() + " INTEGER ,"
                + LocationColumn.END_TIME.toString() + " INTEGER ,"
                + LocationColumn.UPDATED_TIME.toString() + " INTEGER ,"
                + LocationColumn.ACCURACY.toString() + " FLOAT "
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_INTERACTIONS_TABLE = "CREATE TABLE " + TABLE_INTERACTIONS + "("
                + InteractionsColumn.INTERACTION_ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InteractionsColumn.DEVICE_ID.toString() + " TEXT,"
                + InteractionsColumn.FIRST_SEEN.toString() + " INTEGER,"
                + InteractionsColumn.LAST_SEEN.toString() + " INTEGER "
                + ")";
        db.execSQL(CREATE_INTERACTIONS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERACTIONS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new note
    public long addLocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationColumn.LATITUDE.toString(), location.getLatitude());
        values.put(LocationColumn.LONGITUDE.toString(), location.getLongitude());
        values.put(LocationColumn.PROVIDER.toString(), location.getProvider());
        values.put(LocationColumn.ADDRESS_LINE.toString(), location.getAddressLine());
        values.put(LocationColumn.COUNTRY_CODE.toString(), location.getCountryCode());
        values.put(LocationColumn.ADMIN_AREA.toString(), location.getAdminArea());
        values.put(LocationColumn.FEATURE_NAME.toString(), location.getFeatureName());
        values.put(LocationColumn.SUB_AREA_NAME.toString(), location.getSubAdminArea());
        values.put(LocationColumn.START_TIME.toString(),location.getStartTime().getTime());
        values.put(LocationColumn.END_TIME.toString(),location.getEndTime().getTime());
        values.put(LocationColumn.UPDATED_TIME.toString(),location.getUpdateTime().getTime());
        values.put(LocationColumn.ACCURACY.toString(),location.getAccuracy());
        // Inserting Row
        long id = db.insert(TABLE_LOCATIONS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
        return id;
    }

    // code to get the single contact
//    Contact getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }

    // code to get all notes in a list view
    public List<LocationsGroup> getAllLocations(SORTING_PARAM sorting) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<MyLocation> locations = getLocations(db,sorting);
        List<Interaction> interactions = getAllInteractions(db);
        db.close();
        // return locations group list
        return getLocationsGroup(locations,interactions);
    }

    private List<MyLocation> getLocations(SQLiteDatabase db,SORTING_PARAM sorting){
        List<MyLocation> locations = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s FROM %s ",
                LocationColumn.LATITUDE.toString(),
                LocationColumn.LONGITUDE.toString(),
                LocationColumn.PROVIDER.toString(),
                LocationColumn.UPDATED_TIME.toString(),
                LocationColumn.ADDRESS_LINE.toString(),
                LocationColumn.COUNTRY_CODE.toString(),
                LocationColumn.ADMIN_AREA.toString(),
                LocationColumn.FEATURE_NAME.toString(),
                LocationColumn.SUB_AREA_NAME.toString(),
                LocationColumn.START_TIME.toString(),
                LocationColumn.END_TIME.toString(),
                LocationColumn.ACCURACY.toString(),
                TABLE_LOCATIONS);

        if(sorting != null) {
            selectQuery += sorting.getSorting();
        }


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String provider = cursor.getString(2);
                long temp = cursor.getLong(3);
                Date lastUpdated = new Date(temp);

                MyLocation location = new MyLocation(lat,lon,provider,lastUpdated);

                location.setAddressLine(cursor.getString(4));
                location.setCountryCode(cursor.getString(5));
                location.setAdminArea(cursor.getString(6));
                location.setFeatureName(cursor.getString(7));
                location.setSubAdminArea(cursor.getString(8));

                temp = cursor.getLong(9);
                location.setStartTime(new Date(temp));

                temp = cursor.getLong(10);
                location.setEndTime(new Date(temp));
                location.setAccuracy(cursor.getFloat(11));

                locations.add(location);
            } while (cursor.moveToNext());
        }
        return locations;
    }

//    public boolean removeNotes(List<Integer> selectedNotes) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        StringBuilder ids = new StringBuilder();
//        for(int i=0;i<selectedNotes.size();i++) {
//            ids.append(String.valueOf(selectedNotes.get(i))).append(",");
//        }
//
//        String idsToQuery = ids.substring(0,ids.length() - 1);
//
//        String sql = "delete from "+ TABLE_NOTES +
//                " where "+KEY_ID+" in (" + idsToQuery + ")";
//        try {
//            db.execSQL(sql);
//            return true;
//        }catch (Exception e){
//        }
//        db.close();
//        return false;
//    }

    public List<MyLocation> getLocationsOnDay2(SORTING_PARAM sorting, Long time) {
        List<MyLocation> locations = new ArrayList<>();

        Long dayStart = time - time % 86400000; // the remainder of the modulus will be time of day (time since day started)
        Long dayEnd = dayStart + 86400000;

        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s FROM %s WHERE %s > %s AND %s < %s",
                LocationColumn.LATITUDE.toString(),
                LocationColumn.LONGITUDE.toString(),
                LocationColumn.PROVIDER.toString(),
                LocationColumn.UPDATED_TIME.toString(),
                LocationColumn.ADDRESS_LINE.toString(),
                LocationColumn.COUNTRY_CODE.toString(),
                LocationColumn.ADMIN_AREA.toString(),
                LocationColumn.FEATURE_NAME.toString(),
                LocationColumn.SUB_AREA_NAME.toString(),
                LocationColumn.START_TIME.toString(),
                LocationColumn.END_TIME.toString(),
                LocationColumn.ACCURACY.toString(),
                TABLE_LOCATIONS,
                LocationColumn.START_TIME.toString(),
                dayStart.toString(),
                LocationColumn.START_TIME.toString(),
                dayEnd.toString());

        if(sorting != null) {
            selectQuery += sorting.getSorting();
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String provider = cursor.getString(2);
                long temp = cursor.getLong(3);
                Date lastUpdated = new Date(temp);

                MyLocation location = new MyLocation(lat,lon,provider,lastUpdated);

                location.setAddressLine(cursor.getString(4));
                location.setCountryCode(cursor.getString(5));
                location.setAdminArea(cursor.getString(6));
                location.setFeatureName(cursor.getString(7));
                location.setSubAdminArea(cursor.getString(8));

                temp = cursor.getLong(9);
                location.setStartTime(new Date(temp));

                temp = cursor.getLong(10);
                location.setEndTime(new Date(temp));
                location.setAccuracy(cursor.getFloat(11));

                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        // return notes list
        return locations;
    }

    public int getLocationsOnDay(Long time) {
        List<MyLocation> locations = new ArrayList<>();
        Date date = new Date();

        Long dayStart = time - time % 86400000; // the remainder of the modulus will be time of day (time since day started)
        Long dayEnd = dayStart + 86400000;

        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s FROM %s WHERE %s between %s and %s",
                LocationColumn.LATITUDE.toString(),
                LocationColumn.LONGITUDE.toString(),
                LocationColumn.PROVIDER.toString(),
                LocationColumn.UPDATED_TIME.toString(),
                LocationColumn.ADDRESS_LINE.toString(),
                LocationColumn.COUNTRY_CODE.toString(),
                LocationColumn.ADMIN_AREA.toString(),
                LocationColumn.FEATURE_NAME.toString(),
                LocationColumn.SUB_AREA_NAME.toString(),
                LocationColumn.START_TIME.toString(),
                LocationColumn.END_TIME.toString(),
                LocationColumn.ACCURACY.toString(),
                TABLE_LOCATIONS,
                LocationColumn.START_TIME.toString(),
                dayStart.toString(),
                dayEnd.toString());


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // return notes list
        return cursor.getCount();
    }

    public List<MyLocation> getLocationsBetweenDates(SORTING_PARAM sorting, Long from, Long to) {
        List<MyLocation> locations = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s FROM %s WHERE %s > %s AND %s < %s",
                LocationColumn.LATITUDE.toString(),
                LocationColumn.LONGITUDE.toString(),
                LocationColumn.PROVIDER.toString(),
                LocationColumn.UPDATED_TIME.toString(),
                LocationColumn.ADDRESS_LINE.toString(),
                LocationColumn.COUNTRY_CODE.toString(),
                LocationColumn.ADMIN_AREA.toString(),
                LocationColumn.FEATURE_NAME.toString(),
                LocationColumn.SUB_AREA_NAME.toString(),
                LocationColumn.START_TIME.toString(),
                LocationColumn.END_TIME.toString(),
                LocationColumn.ACCURACY.toString(),
                TABLE_LOCATIONS,
                LocationColumn.START_TIME.toString(),
                from.toString(),
                LocationColumn.START_TIME.toString(),
                to.toString());

        if(sorting != null) {
            selectQuery += sorting.getSorting();
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String provider = cursor.getString(2);
                long temp = cursor.getLong(3);
                Date lastUpdated = new Date(temp);

                MyLocation location = new MyLocation(lat,lon,provider,lastUpdated);

                location.setAddressLine(cursor.getString(4));
                location.setCountryCode(cursor.getString(5));
                location.setAdminArea(cursor.getString(6));
                location.setFeatureName(cursor.getString(7));
                location.setSubAdminArea(cursor.getString(8));

                temp = cursor.getLong(9);
                location.setStartTime(new Date(temp));

                temp = cursor.getLong(10);
                location.setEndTime(new Date(temp));
                location.setAccuracy(cursor.getFloat(11));

                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        // return notes list
        return locations;
    }

    public List<MyLocation> getLocationsByMinDuration(SORTING_PARAM sorting, Long duration) {
        List<MyLocation> locations = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s FROM %s WHERE (%s - %s) > %s",
                LocationColumn.LATITUDE.toString(),
                LocationColumn.LONGITUDE.toString(),
                LocationColumn.PROVIDER.toString(),
                LocationColumn.UPDATED_TIME.toString(),
                LocationColumn.ADDRESS_LINE.toString(),
                LocationColumn.COUNTRY_CODE.toString(),
                LocationColumn.ADMIN_AREA.toString(),
                LocationColumn.FEATURE_NAME.toString(),
                LocationColumn.SUB_AREA_NAME.toString(),
                LocationColumn.START_TIME.toString(),
                LocationColumn.END_TIME.toString(),
                LocationColumn.ACCURACY.toString(),
                TABLE_LOCATIONS,
                LocationColumn.END_TIME.toString(),
                LocationColumn.START_TIME.toString(),
                duration.toString());

        if(sorting != null) {
            selectQuery += sorting.getSorting();
        }


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String provider = cursor.getString(2);
                long temp = cursor.getLong(3);
                Date lastUpdated = new Date(temp);

                MyLocation location = new MyLocation(lat,lon,provider,lastUpdated);

                location.setAddressLine(cursor.getString(4));
                location.setCountryCode(cursor.getString(5));
                location.setAdminArea(cursor.getString(6));
                location.setFeatureName(cursor.getString(7));
                location.setSubAdminArea(cursor.getString(8));

                temp = cursor.getLong(9);
                location.setStartTime(new Date(temp));

                temp = cursor.getLong(10);
                location.setEndTime(new Date(temp));
                location.setAccuracy(cursor.getFloat(11));

                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        // return notes list
        return locations;
    }

    public int updateLocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationColumn.END_TIME.toString(), location.getEndTime().getTime());
        values.put(LocationColumn.UPDATED_TIME.toString(), location.getEndTime().getTime());
        values.put(LocationColumn.ACCURACY.toString(), location.getAccuracy());
        values.put(LocationColumn.LATITUDE.toString(),location.getLatitude());
        values.put(LocationColumn.LONGITUDE.toString(),location.getLongitude());

        // updating row
        int success = db.update(TABLE_LOCATIONS, values, LocationColumn.ID.toString() + " = ?",
                new String[] { String.valueOf(location.getId()) });
        db.close();
        return success;
    }



    public long addInteraction(Interaction interaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InteractionsColumn.DEVICE_ID.toString(), interaction.getUuid());
        values.put(InteractionsColumn.FIRST_SEEN.toString(), interaction.getFirstSeen());
        values.put(InteractionsColumn.LAST_SEEN.toString(), interaction.getLastSeen());

        long id = db.insert(TABLE_INTERACTIONS, null, values);
        db.close();

        return id;
    }

    public void updateInteraction(Interaction interaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InteractionsColumn.LAST_SEEN.toString(), interaction.getLastSeen());
        // updating row
        int success = db.update(TABLE_LOCATIONS, values, LocationColumn.ID.toString() + " = ?",
                new String[] { String.valueOf(interaction.getInteractionID()) });
        db.close();
    }


    private List<Interaction> getAllInteractions(SQLiteDatabase db) {
        List<Interaction> interactions = new ArrayList<>();

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


    //code to update the single Note
//    public int updateNote(Note note) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_TITLE, note.getTitle());
//        values.put(KEY_NOTE,note.getNote());
//        values.put(KEY_FAVORITE,note.isFavorite());
//        values.put(KEY_UPDATED_TIME,note.getUpdatedTime().getTime());
//
//        // updating row
//        int success = db.update(TABLE_NOTES, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(note.getId()) });
//        db.close();
//        return success;
//    }

//    public void changeFavorite(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String sql = "update "+ TABLE_NOTES +
//                " set "+KEY_FAVORITE+" = not "+KEY_FAVORITE + " "+
//                " where " +KEY_ID + " = " + id;
//        try {
//            db.execSQL(sql);
//        }catch (Exception e){
//        }
//        db.close();
//    }
}
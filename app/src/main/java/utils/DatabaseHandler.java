package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import enums.LocationColumn;
import models.MyLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wherewasi.db";
    private static final String TABLE_LOCATIONS = "locations";


    public enum SORTING_PARAM{
        LastUpdated( " order by " + LocationColumn.UPDATED_TIME.toString() + " desc"),
        firstUpdate( " order by " + LocationColumn.UPDATED_TIME.toString() + " asc");

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
                + LocationColumn.START_TIME.toString() + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
                + LocationColumn.END_TIME.toString() + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
                + LocationColumn.UPDATED_TIME.toString() + " DATETIME DEFAULT CURRENT_TIMESTAMP "
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new note
    public void addLocation(MyLocation location) {
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

        // Inserting Row
        db.insert(TABLE_LOCATIONS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        Log.i("db_changed","new location");
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
    public List<MyLocation> getAllLocations(SORTING_PARAM sorting) {
        List<MyLocation> locations = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s ",
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
                TABLE_LOCATIONS);

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


                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        // return notes list
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
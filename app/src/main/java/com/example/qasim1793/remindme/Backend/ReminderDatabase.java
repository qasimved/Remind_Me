/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.qasim1793.remindme.Backend;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.qasim1793.remindme.Utils.SharedPrefManager;
import com.google.android.gms.maps.StreetViewPanoramaFragment;

import java.util.ArrayList;
import java.util.List;


public class ReminderDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ReminderDatabase";

    // Table name
    private static final String TABLE_REMINDERS = "ReminderTable";

    private Context context;

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_REPEAT_NO = "repeat_no";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_FAVOURITE = "favourite";
    private static final String KEY_LOCATION_TITLE = "location";
    private static final String KEY_LOCATION_LAT = "lat";
    private static final String KEY_LOCATION_LNG = "lng";
    private static final String KEY_USER = "userId";
    private static final String KEY_LOCATION_STATUS = "status";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_REPEAT + " BOOLEAN,"
                + KEY_REPEAT_NO + " INTEGER,"
                + KEY_REPEAT_TYPE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_FAVOURITE + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN,"
                + KEY_LOCATION_TITLE + " TEXT,"
                + KEY_LOCATION_LAT + " TEXT,"
                + KEY_LOCATION_LNG + " TEXT,"
                + KEY_USER + " INTEGER,"
                + KEY_LOCATION_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_REMINDERS_TABLE);



//        String CREATE_TABLE_LOCATION_REMINDER = "CREATE TABLE LocationReminder (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "title TEXT, " +
//                "description TEXT, " +
//                "lat TEXT, " +
//                "lng TEXT, " +
//                "status TEXT, " +
//                "userId INTEGER)";
//
//        db.execSQL(CREATE_TABLE_LOCATION_REMINDER);



    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        // Create tables again
        onCreate(db);
    }

    // Adding new Reminder
    public int addReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_DATE , reminder.getDate());
        values.put(KEY_TIME , reminder.getTime());
        values.put(KEY_REPEAT , reminder.getRepeat());
        values.put(KEY_REPEAT_NO , reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_CATEGORY, reminder.getReminderCategory());
        values.put(KEY_FAVOURITE, reminder.getFavorite());
        values.put(KEY_LOCATION_TITLE, reminder.getLocationTitle());
        values.put(KEY_LOCATION_LAT, reminder.getLat());
        values.put(KEY_LOCATION_LNG, reminder.getLng());
        values.put(KEY_USER, reminder.getUserId());
        values.put(KEY_LOCATION_STATUS, "true");

        // Inserting Row
        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return (int) ID;
    }



    public int addLocationReminder(LocationReminder reminder){
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title" , reminder.getTitle());
        values.put("description" , reminder.getDescription());
        values.put("lat" , reminder.getLat());
        values.put("lng" , reminder.getLng());
        values.put("userId" , sharedPrefManager.getStringVar("_id"));
        values.put("status" , "true");

        // Inserting Row
        long ID = db.insert("LocationReminder", null, values);
        db.close();
        return (int) ID;
    }



    // Getting single Reminder
    public Reminder getReminder(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[]
                        {
                                KEY_ID,
                                KEY_TITLE,
                                KEY_DATE,
                                KEY_TIME,
                                KEY_REPEAT,
                                KEY_REPEAT_NO,
                                KEY_REPEAT_TYPE,
                                KEY_CATEGORY,
                                KEY_FAVOURITE,
                                KEY_ACTIVE,
                                KEY_LOCATION_TITLE,
                                KEY_LOCATION_LAT,
                                KEY_LOCATION_LNG,
                                KEY_USER
                        }, KEY_ID + "=?",

                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),cursor.getString(9),cursor.getString(7), cursor.getString(8), cursor.getString(10), cursor.getString(11), cursor.getString(12), Integer.parseInt(cursor.getString(13)));

        return reminder;
    }




    // Getting all Reminders
    public List<Reminder> getAllReminders(){
        List<Reminder> reminderList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2));
                reminder.setTime(cursor.getString(3));
                reminder.setRepeat(cursor.getString(4));
                reminder.setRepeatNo(cursor.getString(5));
                reminder.setRepeatType(cursor.getString(6));
                reminder.setActive(cursor.getString(9));
                reminder.setReminderCategory(cursor.getString(7));
                reminder.setFavorite(cursor.getString(8));
                reminder.setLocationTitle(cursor.getString(10));
                reminder.setLat(cursor.getString(11));
                reminder.setLng(cursor.getString(12));
                reminder.setUserId(Integer.parseInt(cursor.getString(13)));

                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }


    public List<LocationReminder> getAllLocationReminder(){
        List<LocationReminder> reminderList = new ArrayList<>();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        String id = sharedPrefManager.getStringVar("_id");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM LocationReminder WHERE userId =?", new String[] {id});


        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                LocationReminder reminder = new LocationReminder();
                reminder.setId(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDescription(cursor.getString(2));
                reminder.setLat(Double.valueOf(cursor.getString(3)));
                reminder.setLng(Double.valueOf(cursor.getString(4)));
                reminder.setUserId(cursor.getString(5));
                reminder.setStatus(cursor.getString(6));

                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

    public List<Reminder> getAllReminderForBGService(){
        List<Reminder> reminderList = new ArrayList<>();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        String id = sharedPrefManager.getStringVar("_id");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM "+TABLE_REMINDERS+" WHERE userId =? AND  status=?", new String[] {id,"true"});


        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2));
                reminder.setTime(cursor.getString(3));
                reminder.setRepeat(cursor.getString(4));
                reminder.setRepeatNo(cursor.getString(5));
                reminder.setRepeatType(cursor.getString(6));
                reminder.setActive(cursor.getString(9));
                reminder.setReminderCategory(cursor.getString(7));
                reminder.setFavorite(cursor.getString(8));
                reminder.setLocationTitle(cursor.getString(10));
                reminder.setLat(cursor.getString(11));
                reminder.setLng(cursor.getString(12));

                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

//    public List<LocationReminder> getAllLocationReminderForBGService(){
//        List<LocationReminder> reminderList = new ArrayList<>();
//        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
//        String id = sharedPrefManager.getStringVar("_id");
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor =  db.rawQuery("SELECT * FROM LocationReminder WHERE userId =? AND  status=?", new String[] {id,"true"});
//
//
//        // Looping through all rows and adding to list
//        if(cursor.moveToFirst()){
//            do{
//                LocationReminder reminder = new LocationReminder();
//                reminder.setId(Integer.parseInt(cursor.getString(0)));
//                reminder.setTitle(cursor.getString(1));
//                reminder.setDescription(cursor.getString(2));
//                reminder.setLat(Double.valueOf(cursor.getString(3)));
//                reminder.setLng(Double.valueOf(cursor.getString(4)));
//                reminder.setUserId(cursor.getString(5));
//                reminder.setStatus(cursor.getString(6));
//
//                // Adding Reminders to list
//                reminderList.add(reminder);
//            } while (cursor.moveToNext());
//        }
//        return reminderList;
//    }

    public LocationReminder getLocationReminder(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery("SELECT * FROM LocationReminder WHERE _id =?", new String[] {id});

        if (cursor != null)
            cursor.moveToFirst();

       LocationReminder locationReminder = new LocationReminder(Integer.valueOf(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Double.valueOf(cursor.getString(3)),Double.valueOf(cursor.getString(4)),cursor.getString(5),cursor.getString(6));

        return locationReminder;
    }



    public List<Reminder> getAllRemindersFav(){
        List<Reminder> reminderList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String fav = "true";

        Cursor cursor =  db.rawQuery("SELECT * FROM "+TABLE_REMINDERS+" WHERE "+KEY_FAVOURITE+" =?", new String[] {fav});

        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2));
                reminder.setTime(cursor.getString(3));
                reminder.setRepeat(cursor.getString(4));
                reminder.setRepeatNo(cursor.getString(5));
                reminder.setRepeatType(cursor.getString(6));
                reminder.setActive(cursor.getString(9));
                reminder.setReminderCategory(cursor.getString(7));
                reminder.setFavorite(cursor.getString(8));
                reminder.setLocationTitle(cursor.getString(10));
                reminder.setLat(cursor.getString(11));
                reminder.setLng(cursor.getString(12));
                reminder.setUserId(Integer.parseInt(cursor.getString(13)));


                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }


    // Getting Reminders Count
    public int getRemindersCount(){
        String countQuery = "SELECT * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating single Reminder
    public int updateReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_DATE , reminder.getDate());
        values.put(KEY_TIME , reminder.getTime());
        values.put(KEY_REPEAT , reminder.getRepeat());
        values.put(KEY_REPEAT_NO , reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_CATEGORY, reminder.getReminderCategory());
        values.put(KEY_FAVOURITE, reminder.getFavorite());
        values.put(KEY_LOCATION_TITLE, reminder.getLocationTitle());
        values.put(KEY_LOCATION_LAT, reminder.getLng());
        values.put(KEY_LOCATION_LNG, reminder.getLng());
        values.put(KEY_USER, reminder.getUserId());

        // Updating row
        return db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
    }


    public int updateLocationReminder(LocationReminder  reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title" , reminder.getTitle());
        values.put("description" , reminder.getDescription());
        values.put("lat" , reminder.getLat());
        values.put("lng" , reminder.getLng());

        // Updating row
        return db.update("LocationReminder", values, "_id" + "=?",
                new String[]{String.valueOf(reminder.getId())});
    }

    // Deleting single Reminder
    public void deleteLocationReminder(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("LocationReminder", "_id" + "=?",
                new String[]{id});
        db.close();
    }


    // Deleting single Reminder
    public void deleteReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
        db.close();
    }

//    public void updateLocationReminderForServicesWithStatus(LocationReminder obj){
//        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
//        String id = sharedPrefManager.getStringVar("_id");
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor =  db.rawQuery("SELECT * FROM LocationReminder WHERE userId =?", new String[] {id});
//
//
//        // Looping through all rows and adding to list
//        if(cursor.moveToFirst()){
//            do{
//                ContentValues values = new ContentValues();
//                if(cursor.getString(6).equals(String.valueOf(obj.getId())))
//                {
//                    values.put("status","false");
//                }
//                else {
//                    values.put("status","true");
//                }
//
//                db.update("LocationReminder", values, "_id" + "=?",
//                        new String[]{cursor.getString(0)});
//
//
//
//            } while (cursor.moveToNext());
//        }
//
//
//    }

    public void updateLocationReminderForServicesWithStatus(Reminder obj){
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        String id = sharedPrefManager.getStringVar("_id");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM "+TABLE_REMINDERS +" WHERE userId =?", new String[] {id});


        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                if(cursor.getString(0).equals(String.valueOf(obj.getID())))
                {
                    values.put(KEY_LOCATION_STATUS,"false");
                }
                else {
                    values.put(KEY_LOCATION_STATUS,"true");
                }

                db.update(TABLE_REMINDERS, values, "_id" + "=?",
                        new String[]{cursor.getString(0)});



            } while (cursor.moveToNext());
        }


    }
}

package com.example.qasim1793.remindme.Backend;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.qasim1793.remindme.Utils.SharedPrefManager;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RemindMe.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;



    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TERM = "CREATE TABLE Users (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    "fullName TEXT, " +
                                                                    "email TEXT, " +
                                                                    "password TEXT, " +
                                                                    "cellNo TEXT, " +
                                                                    "gender TEXT)";

        String CREATE_TABLE_CATEGORY = "CREATE TABLE Categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "type TEXT, " +
                "name TEXT, " +
                "details TEXT)";

        db.execSQL(CREATE_TABLE_TERM);

        db.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    public boolean addUser(String fullName, String email, String password,String cellNo,String gender) {
        if(!isUserExist(email)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("fullName", fullName);
            contentValues.put("email", email);
            contentValues.put("password", password);
            contentValues.put("cellNo", cellNo);
            contentValues.put("gender", gender);
            db.insert("Users", null, contentValues);
            return true;
        }
        return false;
    }


    public boolean editUser(String fullName, String email, String password,String cellNo,String gender) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("fullName", fullName);
            contentValues.put("email", email);
            contentValues.put("password", password);
            contentValues.put("cellNo", cellNo);
            contentValues.put("gender", gender);
            db.update("Users", contentValues, "_id="+sharedPrefManager.getStringVar("_id"),null);
            sharedPrefManager.setStringVar("fullName", fullName);
            sharedPrefManager.setStringVar("email", email);
            sharedPrefManager.setStringVar("password", password);
            sharedPrefManager.setStringVar("cellNo", cellNo);
            sharedPrefManager.setStringVar("gender",gender );
            return true;
    }

    public boolean editPassword(String password) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password);
        db.update("Users", contentValues, "_id="+sharedPrefManager.getStringVar("_id"),null);
        sharedPrefManager.setStringVar("password", password);
        return true;
    }

    public boolean addCategory(String type, String name, String details) {
        if(!isCategoryExist(name)) {
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
            int userId = Integer.valueOf(sharedPrefManager.getStringVar("_id"));
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("userId", userId);
            contentValues.put("type", type);
            contentValues.put("name", name);
            contentValues.put("details", details);
            db.insert("Categories", null, contentValues);
            return true;
        }
        return false;
    }

    public boolean isUserExist(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM Users WHERE email =?", new String[] {email});
        if(res!=null && res.getCount()>0){
            return true;
        }
        return false;
    }

    public boolean isCategoryExist(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM Categories WHERE name =?", new String[] {name});
        if(res!=null && res.getCount()>0){
            return true;
        }
        return false;
    }

    public boolean login(String email,String passwords){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM Users WHERE email =?", new String[] {email});
        if(res!=null && res.getCount()>0){
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this.context);
                res.moveToFirst();
                sharedPrefManager.setStringVar("_id", res.getString(res.getColumnIndexOrThrow("_id")));
                sharedPrefManager.setStringVar("fullName", res.getString(res.getColumnIndexOrThrow("fullName")));
                sharedPrefManager.setStringVar("email", res.getString(res.getColumnIndexOrThrow("email")));
                sharedPrefManager.setStringVar("password", res.getString(res.getColumnIndexOrThrow("password")));
                sharedPrefManager.setStringVar("cellNo", res.getString(res.getColumnIndexOrThrow("cellNo")));
                sharedPrefManager.setStringVar("gender", res.getString(res.getColumnIndexOrThrow("gender")));
            return true;
        }
        return false;
    }

    public ArrayList<String> getCategory(){
        ArrayList<String> category = new ArrayList<String>();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        String id = sharedPrefManager.getStringVar("_id");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM Categories WHERE userId =?", new String[] {id});
        while (res.moveToNext()) {
            category.add(res.getString(res.getColumnIndexOrThrow("name")));
        }
        return category;
    }
}

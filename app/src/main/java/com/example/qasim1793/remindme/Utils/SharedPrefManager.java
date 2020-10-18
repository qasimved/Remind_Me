package com.example.qasim1793.remindme.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;


public class SharedPrefManager {
    private static Context context;
    private static SharedPrefManager sharedPrefManager = null;
    private static String PREF_FIRSTRUN = "firstRun";

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static SharedPrefManager getInstance(Context context){
        if(sharedPrefManager==null){
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    private SharedPreferences getSharedPreference(){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setStringVar(String key, String value){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getStringVar(String key){
        return getSharedPreference().getString(key,"");
    }

    public void setIntVar(String key, int value){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public int getIntVar(String key){
        return getSharedPreference().getInt(key,0);
    }


    public void setLongVar(String key, Long value){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public float getLongVar(String key){
        return getSharedPreference().getFloat(key,0);
    }

    public void setStringSetVar(String key, Set<String> value){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putStringSet(key,value);
        editor.apply();
    }

    public Set<String> getStringSetVar(String key){
        return getSharedPreference().getStringSet(key,null);
    }

    public void logout(){

        getSharedPreference().edit().clear().commit();
    }


    public Boolean getFirstRun() {
        return getSharedPreference().getBoolean(PREF_FIRSTRUN, true);
    }

    public void setFirstRun(boolean firstrun) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(PREF_FIRSTRUN, firstrun);
        editor.apply();
    }

    public String getnremStringVar(String key) {
        String value = getSharedPreference().getString(key, "");
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(key);
        editor.apply();
        return value;
    }

    public int getnremIntVar(String key) {
        int value = getSharedPreference().getInt(key, 0);
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(key);
        editor.apply();
        return value;
    }

    public long getnremLongVar(String key) {
        long value = getSharedPreference().getLong(key, 0);
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(key);
        editor.apply();
        return value;
    }


    public void removeSetString(String key){
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(key);
        editor.apply();
    }


}
package com.shpp.sv.sqlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by SV on 01.04.2016.
 */
public class SettingsHelper {
    private static SettingsHelper savedInstance;
    private SharedPreferences settings;
    private static final String APP_SETTINGS = "app_settings";
    private static final String SETTINGS_NAME = "name";
    private static final String SETTINGS_LOCATION = "location";


    public SettingsHelper(Context context){
        settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static synchronized SettingsHelper getInstance(Context context){
        if (savedInstance == null){
            savedInstance = new SettingsHelper(context);
        }
        return savedInstance;
    }

    public void saveLoggedUser(String name, String location){
        Editor editor = settings.edit();
        editor.putString(SETTINGS_NAME, name);
        editor.putString(SETTINGS_LOCATION, location);
        editor.apply();
    }

    public void deleteLoggedUser(){
        Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public String getLoggedUserName(){
        String name = "";
        if (settings.contains(SETTINGS_NAME)){
            name = settings.getString(SETTINGS_NAME, "");
        }
        return name;
    }

    public String getLoggedUserLocation(){
        String location = "";
        if (settings.contains(SETTINGS_LOCATION)){
            location = settings.getString(SETTINGS_LOCATION, "");
        }
        return location;
    }
}

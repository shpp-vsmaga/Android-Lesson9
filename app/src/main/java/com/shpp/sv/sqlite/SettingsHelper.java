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
    private static final String SETTINGS_ID = "id";
    public static final int ERROR_ID = -1;


    public SettingsHelper(Context context){
        settings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static synchronized SettingsHelper getInstance(Context context){
        if (savedInstance == null){
            savedInstance = new SettingsHelper(context);
        }
        return savedInstance;
    }

    public void saveLoggedUser(int id){
        Editor editor = settings.edit();
        editor.putInt(SETTINGS_ID, id);
        editor.apply();
    }

    public void deleteLoggedUser(){
        Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public int getLoggedUserID(){
        int id = -1;
        if (settings.contains(SETTINGS_ID)){
            id = settings.getInt(SETTINGS_ID, ERROR_ID);
        }
        return id;
    }

}

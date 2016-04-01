package com.shpp.sv.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

/**
 * Created by SV on 29.03.2016.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
    /*Database info*/
    public static final String DATABASE_NAME = "usersDatabase.db";
    private static final int DATABASE_VERSION = 1;

    /*Table names*/
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ISLANDS = "islands";

    /*User table columns*/
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_ISLAND_ID = "islandId";

    /*Islands table columns*/
    private static final String KEY_ISLAND_ID = "_id";
    private static final String KEY_ISLAND_NAME = "nameIsland";

    private static final String LOG_TAG = "svcom";

    private static UserDatabaseHelper savedInstance;

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = String.format("CREATE TABLE %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, " +
                "%s INTEGER REFERENCES %s)", TABLE_USERS, KEY_USER_ID, KEY_USER_NAME, KEY_USER_PASSWORD,
                KEY_USER_ISLAND_ID, TABLE_ISLANDS);

        String CREATE_ISLANDS_TABLE = "CREATE TABLE " + TABLE_ISLANDS +
                "(" +
                KEY_ISLAND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ISLAND_NAME + " TEXT NOT NULL" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ISLANDS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        }
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void addUser(String name, String password, int islandID){

        String INSERT_USER = String.format("INSERT INTO %s(%s, %s, %s) VALUES (\"%s\", \"%s\", %s)",
                TABLE_USERS,
                KEY_USER_NAME, KEY_USER_PASSWORD, KEY_USER_ISLAND_ID,
                name, password, islandID);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(INSERT_USER);

    }

    public static synchronized UserDatabaseHelper getInstance(Context context) {

        if (savedInstance == null) {
            savedInstance = new UserDatabaseHelper(context.getApplicationContext());
        }
        return savedInstance;
    }

    public boolean userIsExist (String name){
        String QUERY = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USERS + " WHERE " +
                KEY_USER_NAME + " = \"" + name + "\"";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    public boolean passwordIsCorrect(String name, String password){
        String QUERY = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USERS + " WHERE " +
                KEY_USER_NAME + " = \"" + name + "\"" + " AND " + KEY_USER_PASSWORD +
                " = \"" + password + "\"";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);

        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }



    public String getUsersPassword(String name){
        String QUERY = "SELECT " + KEY_USER_PASSWORD + " FROM " + TABLE_USERS + " WHERE " +
                KEY_USER_NAME + " = \"" + name + "\"";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);

        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(KEY_USER_PASSWORD));
        cursor.close();
        return password;
    }

    public void toConsole(){

        String QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                TABLE_USERS,
                TABLE_ISLANDS,
                TABLE_USERS, KEY_USER_ISLAND_ID,
                TABLE_ISLANDS, KEY_ISLAND_ID);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);

        while (cursor.moveToNext()){
            Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex(KEY_USER_ID)) +
                    "   " + cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)) +
                    "   " + cursor.getString(cursor.getColumnIndex(KEY_USER_PASSWORD)) +
                    "   " + cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME)));
        }

        cursor.close();
    }

    public String getUsersLocation(String name){
        String location = "";
        String QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s WHERE %s = \"%s\"",
                TABLE_USERS,
                TABLE_ISLANDS,
                TABLE_USERS, KEY_USER_ISLAND_ID,
                TABLE_ISLANDS, KEY_ISLAND_ID,
                KEY_USER_NAME, name);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        cursor.moveToFirst();
        location = cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME));
        return location;
    }

    public boolean islandsIsInDB(){
        String QUERY = "SELECT * FROM " + TABLE_ISLANDS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        boolean result = false;
        if (cursor.moveToFirst()){
            result = true;
        }
        cursor.close();
        return result;
    }

    public void addIslandsToBase(ArrayList<String> islands){
        SQLiteDatabase db = getWritableDatabase();
        for (String island: islands){
            String INSERT_ISLAND = "INSERT INTO " + TABLE_ISLANDS +
                    "(" +
                    KEY_ISLAND_NAME +
                    ") VALUES ( \"" +
                    island + "\")";
            db.execSQL(INSERT_ISLAND);
        }
    }

    public ArrayList<String> getIslandsList(){
        ArrayList<String> islands = new ArrayList<>();
        String QUERY = "SELECT * FROM " + TABLE_ISLANDS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        while (cursor.moveToNext()){
            islands.add(cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME)));
        }
        cursor.close();
        return islands;
    }

    public void updateUserProfile(String name, String newPasword, int newLocation){
        String QUERY = String.format("UPDATE %s SET %s = \"%s\", %s = %s WHERE %s = \"%s\"",
                TABLE_USERS,
                KEY_USER_PASSWORD, newPasword,
                KEY_USER_ISLAND_ID, newLocation,
                KEY_USER_NAME, name);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(QUERY);
    }




}

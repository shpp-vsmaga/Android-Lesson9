package com.shpp.sv.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static final String ISLANDS_FILE_NAME = "islands.txt";

    private static UserDatabaseHelper savedInstance;

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        addIslandsToDB(context);
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
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_PASSWORD, password);
        values.put(KEY_USER_ISLAND_ID, islandID);
        db.insert(TABLE_USERS, null, values);
    }

    public static synchronized UserDatabaseHelper getInstance(Context context) {

        if (savedInstance == null) {
            savedInstance = new UserDatabaseHelper(context.getApplicationContext());
        }
        return savedInstance;
    }

    private void addIslandsToDB(Context context) {

        if (!islandsIsInDB()){
            ArrayList<String> islands = readIslandsFromFile(context);
            addIslandsToBase(islands);
        }
    }

    private ArrayList<String> readIslandsFromFile(Context context) {
        ArrayList<String> islands = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().
                    open(ISLANDS_FILE_NAME)));

            line = br.readLine();
            while (line != null){
                islands.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return islands;
    }

    public boolean userIsExist (String name){
        boolean result = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_NAME},
                KEY_USER_NAME + " = ?",
                new String[]{name},
                null, null, null);

        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        }
        return result;
    }

    public boolean passwordIsCorrect(String name, String password){
        boolean result = false;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_NAME},
                KEY_USER_NAME + " = ? AND " + KEY_USER_PASSWORD + " = ?",
                new String[]{name, password},
                null, null, null);

        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        }
        return result;
    }


    public String getUsersLocation(String name){
        String location = "";
        SQLiteDatabase db = getReadableDatabase();

        String tableForQuery = String.format("%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                TABLE_USERS,
                TABLE_ISLANDS,
                TABLE_USERS, KEY_USER_ISLAND_ID,
                TABLE_ISLANDS, KEY_ISLAND_ID);

        Cursor cursor = db.query(tableForQuery,
                null,
                KEY_USER_NAME + " = ?",
                new String[]{name},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            location = cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME));
            cursor.close();
        }
        return location;
    }

    public boolean islandsIsInDB(){
        String QUERY = "SELECT * FROM " + TABLE_ISLANDS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        boolean result = false;
        if (cursor != null) {
            result = cursor.moveToFirst();
            cursor.close();
        }
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

    public String[] getIslandsList(){
        SQLiteDatabase db = getReadableDatabase();
        int islandsListSize = getIslandsListSize();

        String[] islands = new String[islandsListSize];
        String QUERY = "SELECT * FROM " + TABLE_ISLANDS;
        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null) {
            for (int i = 0; i < islandsListSize; i++) {
                cursor.moveToNext();
                islands[i] = cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME));
            }
            cursor.close();
        }
        return islands;
    }

    public void updateUserProfile(String name, String newPassword, int newLocation){
        String QUERY = String.format("UPDATE %s SET %s = \"%s\", %s = %s WHERE %s = \"%s\"",
                TABLE_USERS,
                KEY_USER_PASSWORD, newPassword,
                KEY_USER_ISLAND_ID, newLocation,
                KEY_USER_NAME, name);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(QUERY);
    }


    public int getIslandsListSize() {
        SQLiteDatabase db = getReadableDatabase();
        int size = 0;
        String QUERY = "SELECT COUNT (*) FROM " + TABLE_ISLANDS;
        Cursor cursor = db.rawQuery(QUERY, null);
        if (cursor != null){
            cursor.moveToFirst();
            size = cursor.getInt(0);
            cursor.close();
        }
        return size;
    }

    public String getUsernameByID(int id){
        String name = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_NAME},
                KEY_USER_ID + " = ?",
                new String[]{Integer.toString(id)},
                null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
        }
        return name;
    }

    public int getUserID(String name){
        int id = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USER_NAME + " = ?",
                new String[]{name},
                null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
        }
        return id;
    }

    public String getUsersLocation(int id){
        String location = "";
        SQLiteDatabase db = getReadableDatabase();

        String tableForQuery = String.format("%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                TABLE_USERS,
                TABLE_ISLANDS,
                TABLE_USERS, KEY_USER_ISLAND_ID,
                TABLE_ISLANDS, KEY_ISLAND_ID);

        Cursor cursor = db.query(tableForQuery,
                null,
                TABLE_USERS + "." + KEY_USER_ID + " = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            location = cursor.getString(cursor.getColumnIndex(KEY_ISLAND_NAME));
            cursor.close();
        }
        return location;
    }
}

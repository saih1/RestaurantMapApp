package com.saihtoo.restaurantmapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Map_database";
    public static final String TABLE_NAME = "Map_table";
    public static final int DATABASE_VERSION = 1;

    public static final String PLACE_ID = "PLACE_ID";
    public static final String PLACE_NAME = "NAME";
    public static final String PLACE_LAT = "LAT";
    public static final String PLACE_LNG = "LNG";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PLACE_ID TEXT, NAME TEXT, LAT REAL, LNG REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addLocation(String name, String place_id, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLACE_ID, place_id);
        cv.put(PLACE_NAME, name);
        cv.put(PLACE_LAT, lat);
        cv.put(PLACE_LNG, lng);
        long newRowID = db.insert(TABLE_NAME, null, cv);
        db.close();
        return newRowID;
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}

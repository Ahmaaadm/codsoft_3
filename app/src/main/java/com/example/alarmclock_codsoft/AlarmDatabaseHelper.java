package com.example.alarmclock_codsoft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1; // Updated to version 2

    public static final String TABLE_NAME = "alarms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_RINGTONE_URI = "ringtone_uri";
    public static final String COLUMN_ENABLED = "enabled";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HOUR + " INTEGER, " +
                COLUMN_MINUTE + " INTEGER, " +
                COLUMN_RINGTONE_URI + " TEXT, " +
                COLUMN_ENABLED + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_ENABLED + " INTEGER DEFAULT 1");
        }
    }

    // Insert a new alarm
    public long insertAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_RINGTONE_URI, alarm.getRingtoneUri());
        values.put(COLUMN_ENABLED, alarm.isEnabled() ? 1 : 0);
        return db.insert(TABLE_NAME, null, values);
    }

    // Update an existing alarm's enabled state
    public int updateAlarm(int id, boolean isEnabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENABLED, isEnabled ? 1 : 0);
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Delete an alarm by ID
    public int deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Retrieve all alarms
    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int hour = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOUR));
                int minute = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTE));
                String ringtoneUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RINGTONE_URI));
                boolean isEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENABLED)) > 0;

                Alarm alarm = new Alarm(id, hour, minute, ringtoneUri, isEnabled);
                alarmList.add(alarm);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return alarmList;
    }

}

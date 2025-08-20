package com.s23010169.ecowastereporter.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CleanerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Cleaner.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_CLEANERS = "cleaners";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_AREA = "area";
    private static final String COLUMN_EXPERIENCE = "experience";
    private static final String COLUMN_REGISTRATION_DATE = "registration_date";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TASKS_COMPLETED = "tasks_completed";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo";

    // Create table query
    private static final String CREATE_CLEANER_TABLE = "CREATE TABLE " + TABLE_CLEANERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_AREA + " TEXT,"
            + COLUMN_EXPERIENCE + " TEXT,"
            + COLUMN_REGISTRATION_DATE + " TEXT,"
            + COLUMN_STATUS + " TEXT,"
            + COLUMN_TASKS_COMPLETED + " INTEGER DEFAULT 0,"
            + COLUMN_RATING + " REAL DEFAULT 0.0,"
            + COLUMN_PROFILE_PHOTO + " TEXT"
            + ")";

    public CleanerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLEANER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLEANERS);
        onCreate(db);
    }

    // Method to add new cleaner
    public long addCleaner(String name, String email, String password, String phone, String area, String experience) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_AREA, area);
        values.put(COLUMN_EXPERIENCE, experience);
        values.put(COLUMN_REGISTRATION_DATE, getCurrentDateTime());
        values.put(COLUMN_STATUS, "active");
        values.put(COLUMN_TASKS_COMPLETED, 0);
        values.put(COLUMN_RATING, 0.0);

        long result = db.insert(TABLE_CLEANERS, null, values);
        db.close();
        return result;
    }

    // Method to get current date time
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Method to check if email exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbSave = db.query(TABLE_CLEANERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = dbSave.getCount() > 0;
        dbSave.close();
        db.close();
        return exists;
    }

    // Method to validate cleaner login
    public boolean checkCleaner(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbSave = db.query(TABLE_CLEANERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=? AND " + COLUMN_STATUS + "=?",
                new String[]{email, password, "active"},
                null, null, null);
        boolean exists = dbSave.getCount() > 0;
        dbSave.close();
        db.close();
        return exists;
    }

    // Method to get cleaner details
    public Cleaner getCleanerByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbSave = db.query(TABLE_CLEANERS,
                null, // null means select all columns
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);

        Cleaner cleaner = null;
        if (dbSave != null && dbSave.moveToFirst()) {
            cleaner = new Cleaner(
                dbSave.getInt(dbSave.getColumnIndex(COLUMN_ID)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_NAME)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_EMAIL)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_PASSWORD)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_PHONE)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_AREA)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_EXPERIENCE)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_REGISTRATION_DATE)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_STATUS)),
                dbSave.getInt(dbSave.getColumnIndex(COLUMN_TASKS_COMPLETED)),
                dbSave.getFloat(dbSave.getColumnIndex(COLUMN_RATING)),
                dbSave.getString(dbSave.getColumnIndex(COLUMN_PROFILE_PHOTO)) // profilePhoto
            );
            dbSave.close();
        }
        db.close();
        return cleaner;
    }

    // Method to get all cleaners
    public List<Cleaner> getAllCleaners() {
        List<Cleaner> cleanerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CLEANERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbSave = db.rawQuery(selectQuery, null);

        if (dbSave.moveToFirst()) {
            do {
                Cleaner cleaner = new Cleaner(
                    dbSave.getInt(dbSave.getColumnIndex(COLUMN_ID)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_NAME)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_EMAIL)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_PASSWORD)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_PHONE)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_AREA)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_EXPERIENCE)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_REGISTRATION_DATE)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_STATUS)),
                    dbSave.getInt(dbSave.getColumnIndex(COLUMN_TASKS_COMPLETED)),
                    dbSave.getFloat(dbSave.getColumnIndex(COLUMN_RATING)),
                    dbSave.getString(dbSave.getColumnIndex(COLUMN_PROFILE_PHOTO)) // profilePhoto
                );
                cleanerList.add(cleaner);
            } while (dbSave.moveToNext());
        }
        dbSave.close();
        db.close();
        return cleanerList;
    }

    // Method to update cleaner details
    public int updateCleaner(Cleaner cleaner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, cleaner.getName());
        values.put(COLUMN_PHONE, cleaner.getPhone());
        values.put(COLUMN_AREA, cleaner.getArea());
        values.put(COLUMN_EXPERIENCE, cleaner.getExperience());
        values.put(COLUMN_STATUS, cleaner.getStatus());
        values.put(COLUMN_TASKS_COMPLETED, cleaner.getTasksCompleted());
        values.put(COLUMN_RATING, cleaner.getRating());

        int result = db.update(TABLE_CLEANERS, values,
                COLUMN_EMAIL + "=?", new String[]{cleaner.getEmail()});
        db.close();
        return result;
    }

    // Method to update cleaner status
    public int updateCleanerStatus(String email, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_CLEANERS, values,
                COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }

    // Method to increment tasks completed
    public int incrementTasksCompleted(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CLEANERS + " SET " + COLUMN_TASKS_COMPLETED + " = " + COLUMN_TASKS_COMPLETED + " + 1 WHERE " + COLUMN_EMAIL + " = ?",
                new String[]{email});
        db.close();
        return 1;
    }

    // Method to update cleaner rating
    public int updateCleanerRating(String email, float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATING, newRating);

        int result = db.update(TABLE_CLEANERS, values,
                COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }

    // Method to update profile photo path
    public int updateProfilePhoto(String email, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PHOTO, photoPath);
        int result = db.update(TABLE_CLEANERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }

    // Method to delete cleaner
    public void deleteCleaner(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLEANERS, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
    }
} 
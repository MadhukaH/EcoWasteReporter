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

public class CitizenDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Citizen.db";
    private static final int DATABASE_VERSION = 2; // Increased version for new column

    // Table name
    private static final String TABLE_CITIZENS = "citizens";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_REGISTRATION_DATE = "registration_date";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo";

    // Create table query
    private static final String CREATE_CITIZEN_TABLE = "CREATE TABLE " + TABLE_CITIZENS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT,"
            + COLUMN_REGISTRATION_DATE + " TEXT,"
            + COLUMN_PROFILE_PHOTO + " TEXT"
            + ")";

    public CitizenDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITIZEN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add profile_photo column to existing table
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_PROFILE_PHOTO + " TEXT");
        }
    }

    // Method to add new citizen
    public long addCitizen(String name, String email, String password, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_REGISTRATION_DATE, getCurrentDateTime());

        long result = db.insert(TABLE_CITIZENS, null, values);
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
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Method to validate citizen login
    public boolean checkCitizen(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Method to get citizen details
    public Citizen getCitizenByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_PHONE, COLUMN_ADDRESS, COLUMN_REGISTRATION_DATE, COLUMN_PROFILE_PHOTO},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);

        Citizen citizen = null;
        if (cursor != null && cursor.moveToFirst()) {
            citizen = new Citizen(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_DATE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_PHOTO))
            );
            cursor.close();
        }
        db.close();
        return citizen;
    }

    // Method to get all citizens
    public List<Citizen> getAllCitizens() {
        List<Citizen> citizenList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CITIZENS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Citizen citizen = new Citizen(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_PHOTO))
                );
                citizenList.add(citizen);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return citizenList;
    }

    // Method to update citizen details
    public int updateCitizen(Citizen citizen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, citizen.getName());
        values.put(COLUMN_PHONE, citizen.getPhone());
        values.put(COLUMN_ADDRESS, citizen.getAddress());

        int result = db.update(TABLE_CITIZENS, values,
                COLUMN_EMAIL + "=?", new String[]{citizen.getEmail()});
        db.close();
        return result;
    }

    // Method to update profile photo
    public int updateProfilePhoto(String email, String profilePhotoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PHOTO, profilePhotoPath);
        
        int result = db.update(TABLE_CITIZENS, values,
                COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }

    // Method to delete citizen
    public void deleteCitizen(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITIZENS, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
    }
} 
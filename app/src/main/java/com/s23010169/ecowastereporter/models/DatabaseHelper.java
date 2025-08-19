package com.s23010169.ecowastereporter.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EcoWasteDB";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BINS = "bins";

    // Column names for users
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";

    // Column names for bins
    private static final String COLUMN_BIN_ID = "id";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_FILL_PERCENTAGE = "fill_percentage";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_STATUS = "status";

    // Create table queries
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PHONE + " TEXT"
            + ")";

    private static final String CREATE_BIN_TABLE = "CREATE TABLE " + TABLE_BINS + "("
            + COLUMN_BIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_FILL_PERCENTAGE + " INTEGER,"
            + COLUMN_DISTANCE + " REAL,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_STATUS + " TEXT DEFAULT 'Empty'"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_BIN_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Add sample bins when database is opened for the first time
        addSampleBins();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BINS);
        onCreate(db);
    }

    // Method to add new user
    public long addUser(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Method to check if email exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Method to validate user login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Method to get bin by ID
    public Bin getBinById(int binId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bin bin = null;
        
        try {
            Cursor cursor = db.query(TABLE_BINS, null,
                    COLUMN_BIN_ID + "=?", new String[]{String.valueOf(binId)},
                    null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    int idIndex = cursor.getColumnIndex(COLUMN_BIN_ID);
                    int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);
                    int fillIndex = cursor.getColumnIndex(COLUMN_FILL_PERCENTAGE);
                    int distanceIndex = cursor.getColumnIndex(COLUMN_DISTANCE);
                    int latIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                    int lngIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
                    int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);
                    
                    if (idIndex >= 0 && locationIndex >= 0 && fillIndex >= 0 && 
                        distanceIndex >= 0 && latIndex >= 0 && lngIndex >= 0 && statusIndex >= 0) {
                        
                        bin = new Bin(
                            cursor.getInt(idIndex),
                            cursor.getString(locationIndex),
                            cursor.getInt(fillIndex),
                            cursor.getDouble(distanceIndex),
                            cursor.getDouble(latIndex),
                            cursor.getDouble(lngIndex),
                            cursor.getString(statusIndex)
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        
        return bin;
    }

    // Method to update bin status
    public boolean updateBinStatus(int binId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);

        // Also update fill percentage to reflect the chosen status, so UI updates visibly
        int mappedFillPercentage = 0;
        if (newStatus != null) {
            switch (newStatus) {
                case "Empty":
                    mappedFillPercentage = 0;
                    break;
                case "Half Full":
                    mappedFillPercentage = 50;
                    break;
                case "Full":
                    mappedFillPercentage = 90;
                    break;
                case "Overflowing":
                    mappedFillPercentage = 100;
                    break;
                default:
                    // leave as-is if unrecognized
                    mappedFillPercentage = -1;
                    break;
            }
        }
        if (mappedFillPercentage >= 0) {
            values.put(COLUMN_FILL_PERCENTAGE, mappedFillPercentage);
        }

        int result = db.update(TABLE_BINS, values, COLUMN_BIN_ID + "=?",
                new String[]{String.valueOf(binId)});
        db.close();
        return result > 0;
    }

    // Method to add a new bin
    public long addBin(String location, int fillPercentage, double distance, 
                      double latitude, double longitude, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_FILL_PERCENTAGE, fillPercentage);
        values.put(COLUMN_DISTANCE, distance);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_STATUS, status);

        long result = db.insert(TABLE_BINS, null, values);
        db.close();
        return result;
    }

    // Method to get all bins
    public List<Bin> getAllBins() {
        List<Bin> bins = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        try {
            Cursor cursor = db.query(TABLE_BINS, null, null, null, null, null, COLUMN_BIN_ID + " ASC");
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        int idIndex = cursor.getColumnIndex(COLUMN_BIN_ID);
                        int locationIndex = cursor.getColumnIndex(COLUMN_LOCATION);
                        int fillIndex = cursor.getColumnIndex(COLUMN_FILL_PERCENTAGE);
                        int distanceIndex = cursor.getColumnIndex(COLUMN_DISTANCE);
                        int latIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                        int lngIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
                        int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);
                        
                        if (idIndex >= 0 && locationIndex >= 0 && fillIndex >= 0 && 
                            distanceIndex >= 0 && latIndex >= 0 && lngIndex >= 0 && statusIndex >= 0) {
                            
                            Bin bin = new Bin(
                                cursor.getInt(idIndex),
                                cursor.getString(locationIndex),
                                cursor.getInt(fillIndex),
                                cursor.getDouble(distanceIndex),
                                cursor.getDouble(latIndex),
                                cursor.getDouble(lngIndex),
                                cursor.getString(statusIndex)
                            );
                            bins.add(bin);
                        }
                    } catch (Exception e) {
                        // Skip this bin if there's an error
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        
        return bins;
    }

    // Method to add sample bin data
    public void addSampleBins() {
        try {
            // Check if bins already exist
            if (getAllBins().size() > 0) {
                return; // Bins already exist
            }

            // Add sample bins with realistic locations and statuses
            addBin("Central Park Main Gate", 25, 0.5, 40.7829, -73.9654, "Empty");
            addBin("Shopping Mall Entrance", 75, 1.2, 40.7589, -73.9851, "Half Full");
            addBin("Bus Station Platform", 90, 0.8, 40.7505, -73.9934, "Full");
            addBin("Public Library", 100, 1.5, 40.7527, -73.9772, "Overflowing");
            addBin("University Campus Center", 15, 2.1, 40.8075, -73.9626, "Empty");
            addBin("Hospital Emergency", 60, 1.8, 40.7589, -73.9851, "Half Full");
            addBin("Restaurant District", 85, 0.9, 40.7505, -73.9934, "Full");
            addBin("Office Building Lobby", 45, 1.3, 40.7527, -73.9772, "Half Full");
            addBin("Residential Complex", 30, 2.5, 40.8075, -73.9626, "Empty");
            addBin("Train Station Exit", 95, 0.7, 40.7589, -73.9851, "Overflowing");
            addBin("City Hall Plaza", 40, 1.0, 40.7505, -73.9934, "Half Full");
            addBin("Sports Stadium", 70, 1.8, 40.7527, -73.9772, "Half Full");
            addBin("Market Square", 80, 0.6, 40.8075, -73.9626, "Full");
            addBin("Parking Garage", 35, 2.2, 40.7829, -73.9654, "Empty");
            addBin("Community Center", 55, 1.4, 40.7589, -73.9851, "Half Full");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
package com.s23010169.ecowastereporter.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

// This helper manages a simple database with users and bins for the app.
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
        Cursor dbSave = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = dbSave.getCount() > 0;
        dbSave.close();
        db.close();
        return exists;
    }

    // Method to validate user login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbSave = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        boolean exists = dbSave.getCount() > 0;
        dbSave.close();
        db.close();
        return exists;
    }

    // Method to get bin by ID
    public Bin getBinById(int binId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bin bin = null;
        
        try {
            Cursor dbSave = db.query(TABLE_BINS, null,
                    COLUMN_BIN_ID + "=?", new String[]{String.valueOf(binId)},
                    null, null, null);
            
            if (dbSave != null && dbSave.moveToFirst()) {
                try {
                    int idIndex = dbSave.getColumnIndex(COLUMN_BIN_ID);
                    int locationIndex = dbSave.getColumnIndex(COLUMN_LOCATION);
                    int fillIndex = dbSave.getColumnIndex(COLUMN_FILL_PERCENTAGE);
                    int distanceIndex = dbSave.getColumnIndex(COLUMN_DISTANCE);
                    int latIndex = dbSave.getColumnIndex(COLUMN_LATITUDE);
                    int lngIndex = dbSave.getColumnIndex(COLUMN_LONGITUDE);
                    int statusIndex = dbSave.getColumnIndex(COLUMN_STATUS);
                    
                    if (idIndex >= 0 && locationIndex >= 0 && fillIndex >= 0 && 
                        distanceIndex >= 0 && latIndex >= 0 && lngIndex >= 0 && statusIndex >= 0) {
                        
                        bin = new Bin(
                            dbSave.getInt(idIndex),
                            dbSave.getString(locationIndex),
                            dbSave.getInt(fillIndex),
                            dbSave.getDouble(distanceIndex),
                            dbSave.getDouble(latIndex),
                            dbSave.getDouble(lngIndex),
                            dbSave.getString(statusIndex)
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (dbSave != null) {
                dbSave.close();
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
            Cursor dbSave = db.query(TABLE_BINS, null, null, null, null, null, COLUMN_BIN_ID + " ASC");
            
            if (dbSave != null && dbSave.moveToFirst()) {
                do {
                    try {
                        int idIndex = dbSave.getColumnIndex(COLUMN_BIN_ID);
                        int locationIndex = dbSave.getColumnIndex(COLUMN_LOCATION);
                        int fillIndex = dbSave.getColumnIndex(COLUMN_FILL_PERCENTAGE);
                        int distanceIndex = dbSave.getColumnIndex(COLUMN_DISTANCE);
                        int latIndex = dbSave.getColumnIndex(COLUMN_LATITUDE);
                        int lngIndex = dbSave.getColumnIndex(COLUMN_LONGITUDE);
                        int statusIndex = dbSave.getColumnIndex(COLUMN_STATUS);
                        
                        if (idIndex >= 0 && locationIndex >= 0 && fillIndex >= 0 && 
                            distanceIndex >= 0 && latIndex >= 0 && lngIndex >= 0 && statusIndex >= 0) {
                            
                            Bin bin = new Bin(
                                dbSave.getInt(idIndex),
                                dbSave.getString(locationIndex),
                                dbSave.getInt(fillIndex),
                                dbSave.getDouble(distanceIndex),
                                dbSave.getDouble(latIndex),
                                dbSave.getDouble(lngIndex),
                                dbSave.getString(statusIndex)
                            );
                            bins.add(bin);
                        }
                    } catch (Exception e) {
                        // Skip this bin if there's an error
                        e.printStackTrace();
                    }
                } while (dbSave.moveToNext());
            }
            
            if (dbSave != null) {
                dbSave.close();
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

            // Add sample bins with realistic locations around Matara Town, Sri Lanka
            addBin("Matara Fort Entrance", 25, 0.3, 5.9483, 80.5353, "Empty");
            addBin("Matara Bus Stand", 75, 0.8, 5.9490, 80.5360, "Half Full");
            addBin("Matara Railway Station", 90, 0.5, 5.9475, 80.5345, "Full");
            addBin("Matara Public Library", 100, 0.7, 5.9495, 80.5370, "Overflowing");
            addBin("Matara University Campus", 15, 1.2, 5.9460, 80.5330, "Empty");
            addBin("Matara General Hospital", 60, 0.9, 5.9500, 80.5380, "Half Full");
            addBin("Matara Market Square", 85, 0.4, 5.9488, 80.5358, "Full");
            addBin("Matara City Hall", 45, 0.6, 5.9485, 80.5355, "Half Full");
            addBin("Matara Beach Park", 30, 0.8, 5.9470, 80.5340, "Empty");
            addBin("Matara Clock Tower", 95, 0.3, 5.9480, 80.5350, "Overflowing");
            addBin("Matara Post Office", 40, 0.5, 5.9492, 80.5365, "Half Full");
            addBin("Matara Police Station", 70, 0.7, 5.9478, 80.5348, "Half Full");
            addBin("Matara Temple Road", 80, 0.4, 5.9482, 80.5352, "Full");
            addBin("Matara Beach Road", 35, 0.9, 5.9465, 80.5335, "Empty");
            addBin("Matara Central Junction", 55, 0.6, 5.9487, 80.5357, "Half Full");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
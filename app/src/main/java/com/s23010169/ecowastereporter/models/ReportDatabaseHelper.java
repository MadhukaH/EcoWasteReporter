package com.s23010169.ecowastereporter.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ReportDatabaseHelper";
    private static final String DATABASE_NAME = "Reports.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_REPORTS = "reports";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_REPORT_ID = "report_id";
    private static final String COLUMN_WASTE_TYPE = "waste_type";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_PHOTO_URIS = "photo_uris";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_STATUS = "status";

    // Create table query
    private static final String CREATE_REPORTS_TABLE = "CREATE TABLE " + TABLE_REPORTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REPORT_ID + " TEXT UNIQUE,"
            + COLUMN_WASTE_TYPE + " TEXT,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_PHOTO_URIS + " TEXT,"
            + COLUMN_TIMESTAMP + " INTEGER,"
            + COLUMN_STATUS + " TEXT"
            + ")";

    private final Gson gson;

    public ReportDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.gson = new Gson();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating reports table");
            db.execSQL(CREATE_REPORTS_TABLE);
            Log.d(TAG, "Reports table created successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error creating reports table", e);
            throw new RuntimeException("Failed to create reports table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            // Backup data if needed before upgrade
            backupDataBeforeUpgrade(db);
            
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
            onCreate(db);
            
            // Restore data if needed after upgrade
            restoreDataAfterUpgrade(db);
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading database", e);
            throw new RuntimeException("Failed to upgrade database", e);
        }
    }

    private void backupDataBeforeUpgrade(SQLiteDatabase db) {
        // Implement data backup logic if needed
        Log.d(TAG, "Backing up data before upgrade");
    }

    private void restoreDataAfterUpgrade(SQLiteDatabase db) {
        // Implement data restoration logic if needed
        Log.d(TAG, "Restoring data after upgrade");
    }

    // Method to add new report
    public long addReport(Report report) {
        if (report == null) {
            Log.e(TAG, "Cannot add null report");
            return -1;
        }

        SQLiteDatabase db = null;
        try {
            Log.d(TAG, "Adding new report with ID: " + report.getReportId());
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_REPORT_ID, report.getReportId());
            values.put(COLUMN_WASTE_TYPE, report.getWasteType());
            values.put(COLUMN_LOCATION, report.getLocation());
            values.put(COLUMN_DESCRIPTION, report.getDescription());
            values.put(COLUMN_LATITUDE, report.getLatitude());
            values.put(COLUMN_LONGITUDE, report.getLongitude());

            // Convert photo URIs to JSON
            String photoUrisJson = null;
            if (report.getPhotoUris() != null) {
                try {
                    photoUrisJson = gson.toJson(report.getPhotoUris());
                } catch (Exception e) {
                    Log.e(TAG, "Error converting photo URIs to JSON", e);
                    photoUrisJson = "[]"; // Default to empty array if conversion fails
                }
            }
            values.put(COLUMN_PHOTO_URIS, photoUrisJson);
            
            values.put(COLUMN_TIMESTAMP, report.getTimestamp());
            values.put(COLUMN_STATUS, report.getStatus());

            long result = db.insert(TABLE_REPORTS, null, values);
            Log.d(TAG, "Report added successfully with row ID: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error adding report", e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to get all reports
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "Fetching all reports");
            db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_REPORTS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Report report = new Report();
                    report.setReportId(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_ID)));
                    report.setWasteType(cursor.getString(cursor.getColumnIndex(COLUMN_WASTE_TYPE)));
                    report.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                    report.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    report.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                    report.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                    
                    // Convert JSON string back to List<Uri>
                    String photoUrisJson = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URIS));
                    try {
                        Type type = new TypeToken<List<Uri>>(){}.getType();
                        List<Uri> photoUris = gson.fromJson(photoUrisJson, type);
                        report.setPhotoUris(photoUris != null ? photoUris : new ArrayList<>());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing photo URIs JSON", e);
                        report.setPhotoUris(new ArrayList<>()); // Set empty list on error
                    }
                    
                    report.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                    report.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                    
                    reports.add(report);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Fetched " + reports.size() + " reports");
            return reports;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching reports", e);
            return new ArrayList<>(); // Return empty list on error
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to update report status
    public int updateReportStatus(String reportId, String newStatus) {
        SQLiteDatabase db = null;
        try {
            Log.d(TAG, "Updating status for report: " + reportId + " to: " + newStatus);
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, newStatus);

            int result = db.update(TABLE_REPORTS, values,
                    COLUMN_REPORT_ID + " = ?",
                    new String[]{reportId});
            Log.d(TAG, "Status updated successfully. Rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error updating report status", e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to delete a report
    public void deleteReport(String reportId) {
        SQLiteDatabase db = null;
        try {
            Log.d(TAG, "Deleting report: " + reportId);
            db = this.getWritableDatabase();
            int result = db.delete(TABLE_REPORTS,
                    COLUMN_REPORT_ID + " = ?",
                    new String[]{reportId});
            Log.d(TAG, "Report deleted successfully. Rows affected: " + result);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting report", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to get total reports count
    public int getTotalReportsCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPORTS, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting total reports count", e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to get resolved reports count
    public int getResolvedReportsCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPORTS + 
                               " WHERE " + COLUMN_STATUS + " = ?", 
                               new String[]{"Resolved"});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting resolved reports count", e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to get recent reports with limit
    public List<Report> getRecentReports(int limit) {
        List<Report> reports = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_REPORTS + 
                               " ORDER BY " + COLUMN_TIMESTAMP + " DESC" +
                               " LIMIT " + limit;
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Report report = new Report();
                    report.setReportId(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_ID)));
                    report.setWasteType(cursor.getString(cursor.getColumnIndex(COLUMN_WASTE_TYPE)));
                    report.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                    report.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    report.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                    report.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                    
                    String photoUrisJson = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URIS));
                    try {
                        Type type = new TypeToken<List<Uri>>(){}.getType();
                        List<Uri> photoUris = gson.fromJson(photoUrisJson, type);
                        report.setPhotoUris(photoUris != null ? photoUris : new ArrayList<>());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing photo URIs JSON", e);
                        report.setPhotoUris(new ArrayList<>());
                    }
                    
                    report.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                    report.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                    
                    reports.add(report);
                } while (cursor.moveToNext());
            }
            return reports;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching recent reports", e);
            return new ArrayList<>();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Method to get a report by its ID
    public Report getReportById(String reportId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_REPORTS, null, COLUMN_REPORT_ID + " = ?", new String[]{reportId}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Report report = new Report();
                report.setReportId(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_ID)));
                report.setWasteType(cursor.getString(cursor.getColumnIndex(COLUMN_WASTE_TYPE)));
                report.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                report.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                report.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                report.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                // Convert JSON string back to List<Uri>
                String photoUrisJson = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URIS));
                try {
                    Type type = new TypeToken<List<Uri>>(){}.getType();
                    List<Uri> photoUris = gson.fromJson(photoUrisJson, type);
                    report.setPhotoUris(photoUris != null ? photoUris : new ArrayList<>());
                } catch (Exception e) {
                    report.setPhotoUris(new ArrayList<>());
                }
                report.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                report.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                return report;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching report by ID", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        return null;
    }
} 
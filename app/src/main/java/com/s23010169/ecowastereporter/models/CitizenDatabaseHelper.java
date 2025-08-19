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
    private static final int DATABASE_VERSION = 3; // Increased version for new columns and rewards tables

    // Table name
    private static final String TABLE_CITIZENS = "citizens";
    private static final String TABLE_REWARDS = "rewards";
    private static final String TABLE_CLAIMED_REWARDS = "claimed_rewards";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_REGISTRATION_DATE = "registration_date";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo";
    private static final String COLUMN_POINTS = "points";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_STREAK = "streak";
    private static final String COLUMN_CURRENT_XP = "current_xp";

    // Rewards table columns
    private static final String COLUMN_REWARD_ID = "id";
    private static final String COLUMN_REWARD_NAME = "name";
    private static final String COLUMN_REWARD_POINTS = "points";
    private static final String COLUMN_REWARD_TYPE = "type";
    private static final String COLUMN_REWARD_ICON = "icon";

    // Claimed rewards columns
    private static final String COLUMN_CLAIM_ID = "id";
    private static final String COLUMN_CLAIM_USER_EMAIL = "user_email";
    private static final String COLUMN_CLAIM_REWARD_ID = "reward_id";
    private static final String COLUMN_CLAIMED_AT = "claimed_at";

    // Create table query
    private static final String CREATE_CITIZEN_TABLE = "CREATE TABLE " + TABLE_CITIZENS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT,"
            + COLUMN_REGISTRATION_DATE + " TEXT,"
            + COLUMN_PROFILE_PHOTO + " TEXT,"
            + COLUMN_POINTS + " INTEGER DEFAULT 0,"
            + COLUMN_LEVEL + " INTEGER DEFAULT 1,"
            + COLUMN_STREAK + " INTEGER DEFAULT 0,"
            + COLUMN_CURRENT_XP + " INTEGER DEFAULT 0"
            + ")";

    private static final String CREATE_REWARDS_TABLE = "CREATE TABLE " + TABLE_REWARDS + "("
            + COLUMN_REWARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_REWARD_NAME + " TEXT,"
            + COLUMN_REWARD_POINTS + " INTEGER,"
            + COLUMN_REWARD_TYPE + " TEXT,"
            + COLUMN_REWARD_ICON + " TEXT"
            + ")";

    private static final String CREATE_CLAIMED_REWARDS_TABLE = "CREATE TABLE " + TABLE_CLAIMED_REWARDS + "("
            + COLUMN_CLAIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CLAIM_USER_EMAIL + " TEXT,"
            + COLUMN_CLAIM_REWARD_ID + " INTEGER,"
            + COLUMN_CLAIMED_AT + " INTEGER"
            + ")";

    public CitizenDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITIZEN_TABLE);
        db.execSQL(CREATE_REWARDS_TABLE);
        db.execSQL(CREATE_CLAIMED_REWARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add profile_photo column to existing table
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_PROFILE_PHOTO + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_POINTS + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_LEVEL + " INTEGER DEFAULT 1");
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_STREAK + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_CITIZENS + " ADD COLUMN " + COLUMN_CURRENT_XP + " INTEGER DEFAULT 0");
            db.execSQL(CREATE_REWARDS_TABLE);
            db.execSQL(CREATE_CLAIMED_REWARDS_TABLE);
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
        values.put(COLUMN_POINTS, 0);
        values.put(COLUMN_LEVEL, 1);
        values.put(COLUMN_STREAK, 0);
        values.put(COLUMN_CURRENT_XP, 0);

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

    // Stats getters and updaters
    public int getUserPoints(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_POINTS}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int points = 0;
        if (cursor != null && cursor.moveToFirst()) {
            points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINTS));
            cursor.close();
        }
        db.close();
        return points;
    }

    public int getUserLevel(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_LEVEL}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int level = 1;
        if (cursor != null && cursor.moveToFirst()) {
            level = cursor.getInt(cursor.getColumnIndex(COLUMN_LEVEL));
            cursor.close();
        }
        db.close();
        return level;
    }

    public int getUserStreak(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_STREAK}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int streak = 0;
        if (cursor != null && cursor.moveToFirst()) {
            streak = cursor.getInt(cursor.getColumnIndex(COLUMN_STREAK));
            cursor.close();
        }
        db.close();
        return streak;
    }

    public int getUserCurrentXp(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CITIZENS, new String[]{COLUMN_CURRENT_XP}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int xp = 0;
        if (cursor != null && cursor.moveToFirst()) {
            xp = cursor.getInt(cursor.getColumnIndex(COLUMN_CURRENT_XP));
            cursor.close();
        }
        db.close();
        return xp;
    }

    public int updateUserStats(String email, int points, int level, int streak, int currentXp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, points);
        values.put(COLUMN_LEVEL, level);
        values.put(COLUMN_STREAK, streak);
        values.put(COLUMN_CURRENT_XP, currentXp);
        int result = db.update(TABLE_CITIZENS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return result;
    }

    // Rewards helpers
    public void seedRewardsIfEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REWARDS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        if (count > 0) {
            db.close();
            return;
        }

        ContentValues v = new ContentValues();
        v.put(COLUMN_REWARD_NAME, "Eco Coffee Cup"); v.put(COLUMN_REWARD_POINTS, 500); v.put(COLUMN_REWARD_TYPE, "item"); v.put(COLUMN_REWARD_ICON, "☕"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Recycling Badge"); v.put(COLUMN_REWARD_POINTS, 1000); v.put(COLUMN_REWARD_TYPE, "badge"); v.put(COLUMN_REWARD_ICON, "♻️"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Tree Planting Day"); v.put(COLUMN_REWARD_POINTS, 2000); v.put(COLUMN_REWARD_TYPE, "experience"); v.put(COLUMN_REWARD_ICON, "🌳"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Eco Workshop"); v.put(COLUMN_REWARD_POINTS, 5000); v.put(COLUMN_REWARD_TYPE, "education"); v.put(COLUMN_REWARD_ICON, "📚"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Solar Charger"); v.put(COLUMN_REWARD_POINTS, 8000); v.put(COLUMN_REWARD_TYPE, "item"); v.put(COLUMN_REWARD_ICON, "☀️"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Beach Cleanup"); v.put(COLUMN_REWARD_POINTS, 1500); v.put(COLUMN_REWARD_TYPE, "experience"); v.put(COLUMN_REWARD_ICON, "🏖️"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Eco Water Bottle"); v.put(COLUMN_REWARD_POINTS, 3000); v.put(COLUMN_REWARD_TYPE, "item"); v.put(COLUMN_REWARD_ICON, "💧"); db.insert(TABLE_REWARDS, null, v);
        v.clear(); v.put(COLUMN_REWARD_NAME, "Green Energy Credits"); v.put(COLUMN_REWARD_POINTS, 800); v.put(COLUMN_REWARD_TYPE, "credits"); v.put(COLUMN_REWARD_ICON, "⚡"); db.insert(TABLE_REWARDS, null, v);
        db.close();
    }

    public List<Reward> getAllRewards() {
        List<Reward> rewards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REWARDS, null, null, null, null, null, COLUMN_REWARD_POINTS + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Reward reward = new Reward(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_REWARD_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_REWARD_NAME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_REWARD_POINTS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_REWARD_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_REWARD_ICON)),
                        false
                );
                rewards.add(reward);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return rewards;
    }

    public List<Integer> getClaimedRewardIdsForUser(String email) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLAIMED_REWARDS, new String[]{COLUMN_CLAIM_REWARD_ID}, COLUMN_CLAIM_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(cursor.getColumnIndex(COLUMN_CLAIM_REWARD_ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids;
    }

    public boolean claimReward(String email, int rewardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLAIM_USER_EMAIL, email);
        values.put(COLUMN_CLAIM_REWARD_ID, rewardId);
        values.put(COLUMN_CLAIMED_AT, System.currentTimeMillis());
        long result = db.insert(TABLE_CLAIMED_REWARDS, null, values);
        db.close();
        return result > 0;
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
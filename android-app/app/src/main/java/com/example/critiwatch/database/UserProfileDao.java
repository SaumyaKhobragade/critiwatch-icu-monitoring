package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.UserProfile;
import com.example.critiwatch.utils.DateTimeUtils;

public class UserProfileDao {

    private static final String DEFAULT_NAME = "Demo Doctor";
    private static final String DEFAULT_EMAIL = "doctor@critiwatch.local";
    private static final String DEFAULT_DESIGNATION = "ICU Resident";

    private final DatabaseHelper databaseHelper;

    public UserProfileDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public void createDefaultProfileIfMissing() {
        UserProfile existing = getProfile();
        if (existing != null) {
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PROFILE_NAME, DEFAULT_NAME);
        values.put(DatabaseHelper.COL_PROFILE_EMAIL, DEFAULT_EMAIL);
        values.put(DatabaseHelper.COL_PROFILE_DESIGNATION, DEFAULT_DESIGNATION);
        values.put(DatabaseHelper.COL_PROFILE_UPDATED_AT, DateTimeUtils.now());
        db.insert(DatabaseHelper.TABLE_USER_PROFILE, null, values);
    }

    public UserProfile getProfile() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USER_PROFILE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COL_ID + " ASC",
                "1"
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToProfile(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public long saveOrUpdateProfile(UserProfile profile) {
        if (profile == null) {
            return -1L;
        }

        String name = profile.getName() == null ? "" : profile.getName().trim();
        String email = profile.getEmail() == null ? "" : profile.getEmail().trim();
        String designation = profile.getDesignation() == null ? "" : profile.getDesignation().trim();
        if (name.isEmpty() || email.isEmpty() || designation.isEmpty()) {
            return -1L;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PROFILE_NAME, name);
        values.put(DatabaseHelper.COL_PROFILE_EMAIL, email);
        values.put(DatabaseHelper.COL_PROFILE_DESIGNATION, designation);
        values.put(DatabaseHelper.COL_PROFILE_UPDATED_AT, DateTimeUtils.now());

        UserProfile existing = getProfile();
        int providedId = parseId(profile.getId());
        int targetId = providedId > 0 ? providedId : (existing == null ? -1 : parseId(existing.getId()));

        if (targetId > 0) {
            int rows = db.update(
                    DatabaseHelper.TABLE_USER_PROFILE,
                    values,
                    DatabaseHelper.COL_ID + "=?",
                    new String[]{String.valueOf(targetId)}
            );
            return rows > 0 ? targetId : -1L;
        }

        return db.insert(DatabaseHelper.TABLE_USER_PROFILE, null, values);
    }

    private UserProfile mapCursorToProfile(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_EMAIL));
        String designation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_DESIGNATION));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_UPDATED_AT));
        return new UserProfile(
                String.valueOf(id),
                name,
                email,
                designation,
                updatedAt
        );
    }

    private int parseId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}

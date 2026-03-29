package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.VitalSign;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class VitalDao {

    private final DatabaseHelper databaseHelper;

    public VitalDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertVital(VitalSign vitalSign) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_VITAL_PATIENT_ID, parseId(vitalSign.getPatientId()));
        values.put(DatabaseHelper.COL_VITAL_HEART_RATE, vitalSign.getHeartRate());
        values.put(DatabaseHelper.COL_VITAL_SPO2, vitalSign.getSpo2());
        values.put(DatabaseHelper.COL_VITAL_SYSTOLIC_BP, vitalSign.getSystolicBp());
        values.put(DatabaseHelper.COL_VITAL_DIASTOLIC_BP, vitalSign.getDiastolicBp());
        values.put(DatabaseHelper.COL_VITAL_RESP_RATE, vitalSign.getRespiratoryRate());
        values.put(DatabaseHelper.COL_VITAL_TEMPERATURE, vitalSign.getTemperature());
        values.put(DatabaseHelper.COL_VITAL_HEIGHT_CM, vitalSign.getHeightCm());
        values.put(DatabaseHelper.COL_VITAL_WEIGHT_KG, vitalSign.getWeightKg());
        values.put(DatabaseHelper.COL_VITAL_RECORDED_AT,
                vitalSign.getTimestamp() == null || vitalSign.getTimestamp().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : vitalSign.getTimestamp());
        return db.insert(DatabaseHelper.TABLE_VITAL_SIGNS, null, values);
    }

    public List<VitalSign> getVitalsByPatientId(int patientId) {
        List<VitalSign> vitals = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VITAL_SIGNS,
                null,
                DatabaseHelper.COL_VITAL_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_VITAL_RECORDED_AT + " DESC"
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                vitals.add(mapCursorToVital(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return vitals;
    }

    public VitalSign getLatestVitalByPatientId(int patientId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_VITAL_SIGNS,
                null,
                DatabaseHelper.COL_VITAL_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_VITAL_RECORDED_AT + " DESC",
                "1"
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToVital(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public int deleteVitalsByPatientId(int patientId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_VITAL_SIGNS,
                DatabaseHelper.COL_VITAL_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)}
        );
    }

    private VitalSign mapCursorToVital(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_PATIENT_ID));
        int heartRate = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_HEART_RATE));
        int spo2 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_SPO2));
        int systolic = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_SYSTOLIC_BP));
        int diastolic = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_DIASTOLIC_BP));
        int rr = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_RESP_RATE));
        double temp = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_TEMPERATURE));
        double height = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_HEIGHT_CM));
        double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_WEIGHT_KG));
        String recordedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VITAL_RECORDED_AT));

        return new VitalSign(
                String.valueOf(id),
                String.valueOf(patientId),
                heartRate,
                spo2,
                systolic,
                diastolic,
                rr,
                temp,
                height,
                weight,
                recordedAt
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

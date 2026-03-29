package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.Patient;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    private final DatabaseHelper databaseHelper;

    public PatientDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertPatient(Patient patient) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PATIENT_NAME, patient.getName());
        values.put(DatabaseHelper.COL_PATIENT_AGE, patient.getAge());
        values.put(DatabaseHelper.COL_PATIENT_SEX, patient.getSex());
        values.put(DatabaseHelper.COL_PATIENT_WARD, patient.getWard());
        values.put(DatabaseHelper.COL_PATIENT_BED_NUMBER, patient.getBedNumber());
        values.put(DatabaseHelper.COL_PATIENT_RISK_LEVEL, patient.getRiskLevel());
        values.put(DatabaseHelper.COL_PATIENT_CREATED_AT,
                patient.getCreatedAt() == null || patient.getCreatedAt().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : patient.getCreatedAt());
        return db.insert(DatabaseHelper.TABLE_PATIENTS, null, values);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PATIENTS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COL_ID + " DESC"
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                patients.add(mapCursorToPatient(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return patients;
    }

    public Patient getPatientById(int id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PATIENTS,
                null,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1"
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToPatient(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public int updatePatient(Patient patient) {
        int patientId = parseId(patient.getId());
        if (patientId <= 0) {
            return 0;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PATIENT_NAME, patient.getName());
        values.put(DatabaseHelper.COL_PATIENT_AGE, patient.getAge());
        values.put(DatabaseHelper.COL_PATIENT_SEX, patient.getSex());
        values.put(DatabaseHelper.COL_PATIENT_WARD, patient.getWard());
        values.put(DatabaseHelper.COL_PATIENT_BED_NUMBER, patient.getBedNumber());
        values.put(DatabaseHelper.COL_PATIENT_RISK_LEVEL, patient.getRiskLevel());
        return db.update(
                DatabaseHelper.TABLE_PATIENTS,
                values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(patientId)}
        );
    }

    public int deletePatient(int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PATIENTS,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    private Patient mapCursorToPatient(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_NAME));
        int age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_AGE));
        String sex = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_SEX));
        String ward = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_WARD));
        String bed = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_BED_NUMBER));
        String risk = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_RISK_LEVEL));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENT_CREATED_AT));

        return new Patient(
                String.valueOf(id),
                name,
                age,
                sex,
                ward,
                bed,
                risk,
                createdAt
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

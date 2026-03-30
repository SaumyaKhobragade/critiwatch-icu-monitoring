package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.PatientNote;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    private final DatabaseHelper databaseHelper;

    public NoteDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertNote(PatientNote note) {
        int patientId = parseId(note.getPatientId());
        if (patientId <= 0 || note.getNoteText() == null || note.getNoteText().trim().isEmpty()) {
            return -1L;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NOTE_PATIENT_ID, patientId);
        values.put(DatabaseHelper.COL_NOTE_TEXT, note.getNoteText().trim());
        values.put(
                DatabaseHelper.COL_NOTE_CREATED_AT,
                note.getCreatedAt() == null || note.getCreatedAt().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : note.getCreatedAt().trim()
        );
        return db.insert(DatabaseHelper.TABLE_PATIENT_NOTES, null, values);
    }

    public PatientNote getLatestNoteByPatientId(int patientId) {
        if (patientId <= 0) {
            return null;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PATIENT_NOTES,
                null,
                DatabaseHelper.COL_NOTE_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_ID + " DESC",
                "1"
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToNote(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<PatientNote> getNotesByPatientId(int patientId) {
        List<PatientNote> notes = new ArrayList<>();
        if (patientId <= 0) {
            return notes;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PATIENT_NOTES,
                null,
                DatabaseHelper.COL_NOTE_PATIENT_ID + "=?",
                new String[]{String.valueOf(patientId)},
                null,
                null,
                DatabaseHelper.COL_ID + " DESC"
        );

        try {
            while (cursor != null && cursor.moveToNext()) {
                notes.add(mapCursorToNote(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return notes;
    }

    private PatientNote mapCursorToNote(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_PATIENT_ID));
        String noteText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_TEXT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_CREATED_AT));
        return new PatientNote(
                String.valueOf(id),
                String.valueOf(patientId),
                noteText,
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

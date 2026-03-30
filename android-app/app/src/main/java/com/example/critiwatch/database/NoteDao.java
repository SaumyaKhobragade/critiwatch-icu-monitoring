package com.example.critiwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.critiwatch.models.ClinicalNote;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    private final DatabaseHelper databaseHelper;

    public NoteDao(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public long insertNote(ClinicalNote note) {
        int patientId = parseId(note.getPatientId());
        if (patientId <= 0 || note.getNoteText() == null || note.getNoteText().trim().isEmpty()) {
            return -1L;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String now = DateTimeUtils.now();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NOTE_PATIENT_ID, patientId);
        values.put(DatabaseHelper.COL_NOTE_TEXT, note.getNoteText().trim());
        values.put(
                DatabaseHelper.COL_NOTE_CREATED_AT,
                note.getCreatedAt() == null || note.getCreatedAt().trim().isEmpty()
                        ? now
                        : note.getCreatedAt().trim()
        );
        values.put(
                DatabaseHelper.COL_NOTE_UPDATED_AT,
                note.getUpdatedAt() == null || note.getUpdatedAt().trim().isEmpty()
                        ? now
                        : note.getUpdatedAt().trim()
        );
        return db.insert(DatabaseHelper.TABLE_PATIENT_NOTES, null, values);
    }

    public int updateNote(ClinicalNote note) {
        int noteId = parseId(note.getId());
        if (noteId <= 0 || note.getNoteText() == null || note.getNoteText().trim().isEmpty()) {
            return 0;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NOTE_TEXT, note.getNoteText().trim());
        values.put(
                DatabaseHelper.COL_NOTE_UPDATED_AT,
                note.getUpdatedAt() == null || note.getUpdatedAt().trim().isEmpty()
                        ? DateTimeUtils.now()
                        : note.getUpdatedAt().trim()
        );
        return db.update(
                DatabaseHelper.TABLE_PATIENT_NOTES,
                values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(noteId)}
        );
    }

    public int deleteNote(int noteId) {
        if (noteId <= 0) {
            return 0;
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PATIENT_NOTES,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(noteId)}
        );
    }

    public ClinicalNote getLatestNoteByPatientId(int patientId) {
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

    public List<ClinicalNote> getNotesByPatientId(int patientId) {
        List<ClinicalNote> notes = new ArrayList<>();
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

    private ClinicalNote mapCursorToNote(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        int patientId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_PATIENT_ID));
        String noteText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_TEXT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_CREATED_AT));
        String updatedAt = getStringOrNull(cursor, DatabaseHelper.COL_NOTE_UPDATED_AT);
        return new ClinicalNote(
                String.valueOf(id),
                String.valueOf(patientId),
                noteText,
                createdAt,
                updatedAt
        );
    }

    private String getStringOrNull(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return null;
        }
        return cursor.getString(index);
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

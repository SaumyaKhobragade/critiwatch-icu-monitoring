package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.NoteDao;
import com.example.critiwatch.models.ClinicalNote;
import com.example.critiwatch.utils.DateTimeUtils;

import java.util.List;

public class NoteRepository {

    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        this.noteDao = new NoteDao(context);
    }

    public long addNote(ClinicalNote note) {
        return noteDao.insertNote(note);
    }

    public ClinicalNote getLatestNoteByPatientId(int patientId) {
        return noteDao.getLatestNoteByPatientId(patientId);
    }

    public List<ClinicalNote> getNotesByPatientId(int patientId) {
        return noteDao.getNotesByPatientId(patientId);
    }

    public boolean deleteNote(int noteId) {
        return noteDao.deleteNote(noteId) > 0;
    }

    public ClinicalNote saveOrUpdateLatestNote(int patientId, String noteText) {
        if (patientId <= 0 || noteText == null || noteText.trim().isEmpty()) {
            return null;
        }

        ClinicalNote latest = noteDao.getLatestNoteByPatientId(patientId);
        String now = DateTimeUtils.now();
        if (latest == null) {
            ClinicalNote toInsert = new ClinicalNote(
                    String.valueOf(patientId),
                    noteText.trim(),
                    now,
                    now
            );
            long rowId = noteDao.insertNote(toInsert);
            if (rowId <= 0) {
                return null;
            }
            return noteDao.getLatestNoteByPatientId(patientId);
        }

        latest.setNoteText(noteText.trim());
        latest.setUpdatedAt(now);
        int rows = noteDao.updateNote(latest);
        if (rows <= 0) {
            return null;
        }
        return noteDao.getLatestNoteByPatientId(patientId);
    }
}

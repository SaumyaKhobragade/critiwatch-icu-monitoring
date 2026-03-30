package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.NoteDao;
import com.example.critiwatch.models.ClinicalNote;

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
}

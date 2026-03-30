package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.NoteDao;
import com.example.critiwatch.models.PatientNote;

public class NoteRepository {

    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        this.noteDao = new NoteDao(context);
    }

    public long addNote(PatientNote note) {
        return noteDao.insertNote(note);
    }

    public PatientNote getLatestNoteByPatientId(int patientId) {
        return noteDao.getLatestNoteByPatientId(patientId);
    }
}

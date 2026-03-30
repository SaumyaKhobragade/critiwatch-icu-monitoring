package com.example.critiwatch.models;

public class PatientNote {
    private String id;
    private String patientId;
    private String noteText;
    private String createdAt;

    public PatientNote() {
    }

    public PatientNote(String id, String patientId, String noteText, String createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    public PatientNote(String patientId, String noteText, String createdAt) {
        this.patientId = patientId;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

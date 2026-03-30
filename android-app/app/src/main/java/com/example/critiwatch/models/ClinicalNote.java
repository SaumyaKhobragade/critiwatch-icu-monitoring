package com.example.critiwatch.models;

public class ClinicalNote {

    private String id;
    private String patientId;
    private String noteText;
    private String createdAt;
    private String updatedAt;

    public ClinicalNote() {
    }

    public ClinicalNote(String id, String patientId, String noteText, String createdAt, String updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.noteText = noteText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ClinicalNote(String patientId, String noteText, String createdAt, String updatedAt) {
        this.patientId = patientId;
        this.noteText = noteText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

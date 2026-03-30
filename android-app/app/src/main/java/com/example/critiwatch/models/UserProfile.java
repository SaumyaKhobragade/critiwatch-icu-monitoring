package com.example.critiwatch.models;

public class UserProfile {

    private String id;
    private String name;
    private String email;
    private String designation;
    private String updatedAt;

    public UserProfile() {
    }

    public UserProfile(String id, String name, String email, String designation, String updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.updatedAt = updatedAt;
    }

    public UserProfile(String name, String email, String designation, String updatedAt) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

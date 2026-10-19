package com.communityskillfinder.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String name;
    private String email;
    private String bio;
    private Timestamp createdAt;

    public User() {}

    public User(int userId, String name, String email, String bio, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public User(String name, String email, String bio) {
        this.name = name;
        this.email = email;
        this.bio = bio;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", userId, name, email, (bio != null ? bio : ""));
    }
}

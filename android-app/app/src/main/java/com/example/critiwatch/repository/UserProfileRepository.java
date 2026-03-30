package com.example.critiwatch.repository;

import android.content.Context;

import com.example.critiwatch.database.UserProfileDao;
import com.example.critiwatch.models.UserProfile;

public class UserProfileRepository {

    private final UserProfileDao userProfileDao;

    public UserProfileRepository(Context context) {
        this.userProfileDao = new UserProfileDao(context);
    }

    public void createDefaultProfileIfMissing() {
        userProfileDao.createDefaultProfileIfMissing();
    }

    public UserProfile getProfile() {
        return userProfileDao.getProfile();
    }

    public UserProfile getOrCreateProfile() {
        createDefaultProfileIfMissing();
        return getProfile();
    }

    public boolean saveProfile(UserProfile profile) {
        return userProfileDao.saveOrUpdateProfile(profile) > 0;
    }
}


package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.ProfileDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.Profile;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileRepository {

    private final ProfileDao profileDao;
    private final LiveData<List<Profile>> allProfiles;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public ProfileRepository(Application application) {
        AppDatabase database =
                DatabaseProvider.getDatabase(application);

        profileDao = database.profileDao();
        allProfiles = profileDao.getAllProfiles();
    }

    // Retrieve all profiles
    public LiveData<List<Profile>> getAllProfiles() {
        return allProfiles;
    }

    // Retrieve a profile by its ID
    public LiveData<Profile> getProfileById(int id) {
        return profileDao.getProfileById(id);
    }

    // Insert a profile
    public void insert(Profile profile) {
        executorService.execute(() -> profileDao.insert(profile));
    }

    // Update a profile
    public void update(Profile profile) {
        executorService.execute(() -> profileDao.update(profile));
    }

    // Delete a profile
    public void delete(Profile profile) {
        executorService.execute(() -> profileDao.delete(profile));
    }

    // Delete all profiles
    public void deleteAllProfiles() {
        executorService.execute(() -> profileDao.deleteAllProfiles());
    }
}

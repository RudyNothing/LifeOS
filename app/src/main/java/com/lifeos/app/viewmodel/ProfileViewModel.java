
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.Profile;
import com.lifeos.app.data.repository.ProfileRepository;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository profileRepository;
    private final LiveData<List<Profile>> allProfiles;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        profileRepository = new ProfileRepository(application);
        allProfiles = profileRepository.getAllProfiles();
    }

    // Retrieve all profiles
    public LiveData<List<Profile>> getAllProfiles() {
        return allProfiles;
    }

    // Retrieve a profile by its ID
    public LiveData<Profile> getProfileById(int id) {
        return profileRepository.getProfileById(id);
    }

    // Insert a profile
    public void insert(Profile profile) {
        profileRepository.insert(profile);
    }

    // Update a profile
    public void update(Profile profile) {
        profileRepository.update(profile);
    }

    // Delete a profile
    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

    // Delete all profiles
    public void deleteAllProfiles() {
        profileRepository.deleteAllProfiles();
    }
}

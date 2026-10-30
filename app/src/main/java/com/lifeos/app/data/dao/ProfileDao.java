package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.Profile;

import java.util.List;

@Dao
public interface ProfileDao{
    @Insert
    void insert(Profile profile);

    @Update
    void update(Profile profile);

    @Delete
    void delete(Profile profile);

    @Query("SELECT * FROM profile")
    LiveData<List<Profile>> getAllProfiles();

    @Query("SELECT * FROM profile WHERE id = :id LIMIT 1")
    LiveData<Profile> getProfileById(int id);

    @Query("DELETE FROM profile")
    void deleteAllProfiles();
}

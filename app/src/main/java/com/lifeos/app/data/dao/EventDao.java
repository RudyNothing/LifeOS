package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.Event;

import java.util.List;

@Dao
public interface EventDao{
    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM events")
    List<Event> getAllEventsForRecovery();
    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    LiveData<Event> getEventById(int id);

    @Query("DELETE FROM events")
    void deleteAllEvents();
}

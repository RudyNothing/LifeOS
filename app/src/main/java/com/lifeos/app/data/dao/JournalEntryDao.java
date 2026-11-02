package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.JournalEntry;

import java.util.List;

@Dao
public interface JournalEntryDao{
    @Insert
    void insert(JournalEntry entry);

    @Update
    void update(JournalEntry entry);

    @Delete
    void delete(JournalEntry entry);

    @Query("SELECT * FROM journal_entries")
    LiveData<List<JournalEntry>> getAllEntries();

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    LiveData<JournalEntry> getEntryById(int id);

    @Query("DELETE FROM journal_entries")
    void deleteAllEntries();
}

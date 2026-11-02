
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.JournalEntry;
import com.lifeos.app.data.repository.JournalEntryRepository;

import java.util.List;

public class JournalEntryViewModel extends AndroidViewModel {

    private final JournalEntryRepository journalEntryRepository;
    private final LiveData<List<JournalEntry>> allEntries;

    public JournalEntryViewModel(@NonNull Application application) {
        super(application);

        journalEntryRepository = new JournalEntryRepository(application);
        allEntries = journalEntryRepository.getAllEntries();
    }

    // Retrieve all journal entries and memories
    public LiveData<List<JournalEntry>> getAllEntries() {
        return allEntries;
    }

    // Retrieve an entry by its ID
    public LiveData<JournalEntry> getEntryById(int id) {
        return journalEntryRepository.getEntryById(id);
    }

    // Insert a journal entry or memory
    public void insert(JournalEntry entry) {
        journalEntryRepository.insert(entry);
    }

    // Update an entry
    public void update(JournalEntry entry) {
        journalEntryRepository.update(entry);
    }

    // Delete an entry
    public void delete(JournalEntry entry) {
        journalEntryRepository.delete(entry);
    }

    // Delete all entries
    public void deleteAllEntries() {
        journalEntryRepository.deleteAllEntries();
    }
}

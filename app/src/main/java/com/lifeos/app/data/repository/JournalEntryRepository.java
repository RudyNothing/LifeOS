
package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.JournalEntryDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.JournalEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalEntryRepository {

    private final JournalEntryDao journalEntryDao;
    private final LiveData<List<JournalEntry>> allEntries;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public JournalEntryRepository(Application application) {
        AppDatabase database =
                DatabaseProvider.getDatabase(application);

        journalEntryDao = database.journalEntryDao();
        allEntries = journalEntryDao.getAllEntries();
    }

    // Retrieve all journal entries and memories
    public LiveData<List<JournalEntry>> getAllEntries() {
        return allEntries;
    }

    // Retrieve an entry by its ID
    public LiveData<JournalEntry> getEntryById(int id) {
        return journalEntryDao.getEntryById(id);
    }

    // Insert a journal entry or memory
    public void insert(JournalEntry entry) {
        executorService.execute(() -> journalEntryDao.insert(entry));
    }

    // Update an entry
    public void update(JournalEntry entry) {
        executorService.execute(() -> journalEntryDao.update(entry));
    }

    // Delete an entry
    public void delete(JournalEntry entry) {
        executorService.execute(() -> journalEntryDao.delete(entry));
    }

    // Delete all entries
    public void deleteAllEntries() {
        executorService.execute(
                () -> journalEntryDao.deleteAllEntries()
        );
    }
}

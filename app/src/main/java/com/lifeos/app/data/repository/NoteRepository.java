
package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.NoteDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public NoteRepository(Application application) {
        AppDatabase database =
                DatabaseProvider.getDatabase(application);

        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    // Retrieve all notes
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    // Retrieve a note by its ID
    public LiveData<Note> getNoteById(int id) {
        return noteDao.getNoteById(id);
    }

    // Insert a note
    public void insert(Note note) {
        executorService.execute(() -> noteDao.insert(note));
    }

    // Update a note
    public void update(Note note) {
        executorService.execute(() -> noteDao.update(note));
    }

    // Delete a note
    public void delete(Note note) {
        executorService.execute(() -> noteDao.delete(note));
    }

    // Delete all notes
    public void deleteAllNotes() {
        executorService.execute(() -> noteDao.deleteAllNotes());
    }
}

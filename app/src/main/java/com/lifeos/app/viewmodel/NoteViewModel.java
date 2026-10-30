
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.Note;
import com.lifeos.app.data.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository noteRepository;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();
    }

    // Retrieve all notes
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    // Retrieve a note by its ID
    public LiveData<Note> getNoteById(int id) {
        return noteRepository.getNoteById(id);
    }

    // Insert a note
    public void insert(Note note) {
        noteRepository.insert(note);
    }

    // Update a note
    public void update(Note note) {
        noteRepository.update(note);
    }

    // Delete a note
    public void delete(Note note) {
        noteRepository.delete(note);
    }

    // Delete all notes
    public void deleteAllNotes() {
        noteRepository.deleteAllNotes();
    }
}

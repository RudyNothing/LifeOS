package com.lifeos.app.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lifeos.app.data.dao.EventDao;
import com.lifeos.app.data.dao.FinanceTransactionDao;
import com.lifeos.app.data.dao.JournalEntryDao;
import com.lifeos.app.data.dao.NoteDao;
import com.lifeos.app.data.dao.ProfileDao;
import com.lifeos.app.data.dao.TaskDao;
import com.lifeos.app.data.dao.VaultItemDao;

import com.lifeos.app.data.entity.Event;

import com.lifeos.app.data.entity.FinanceTransaction;
import com.lifeos.app.data.entity.JournalEntry;
import com.lifeos.app.data.entity.Note;
import com.lifeos.app.data.entity.Profile;
import com.lifeos.app.data.entity.Task;
import com.lifeos.app.data.entity.VaultItem;

@Database(
        entities = {
                Task.class,
                Event.class,
                FinanceTransaction.class,
                Note.class,
                JournalEntry.class,
                VaultItem.class,
                Profile.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    public abstract EventDao eventDao();

    public abstract FinanceTransactionDao financeTransactionDao();

    public abstract NoteDao noteDao();

    public abstract JournalEntryDao journalEntryDao();

    public abstract VaultItemDao vaultItemDao();

    public abstract ProfileDao profileDao();
}
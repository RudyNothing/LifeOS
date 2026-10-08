package com.lifeos.app.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lifeos.app.data.dao.TaskDao;
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
}
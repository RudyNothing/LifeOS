package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.VaultItem;

import java.util.List;

@Dao
public interface VaultItemDao {
    @Insert
    void insert(VaultItem item);

    @Update
    void update(VaultItem item);

    @Delete
    void delete(VaultItem item);

    @Query("SELECT * FROM vault_items")
    LiveData<List<VaultItem>> getAllItems();

    @Query("SELECT * FROM vault_items WHERE id = :id LIMIT 1")
    LiveData<VaultItem> getItemById(int id);

    @Query("DELETE from vault_items")
    void deleteAllItems();
}

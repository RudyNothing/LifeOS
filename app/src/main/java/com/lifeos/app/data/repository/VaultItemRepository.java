
package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.VaultItemDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.VaultItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VaultItemRepository {

    private final VaultItemDao vaultItemDao;
    private final LiveData<List<VaultItem>> allItems;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public VaultItemRepository(Application application) {
        AppDatabase database =
                DatabaseProvider.getDatabase(application);

        vaultItemDao = database.vaultItemDao();
        allItems = vaultItemDao.getAllItems();
    }

    // Retrieve all vault items
    public LiveData<List<VaultItem>> getAllItems() {
        return allItems;
    }

    // Retrieve a vault item by its ID
    public LiveData<VaultItem> getItemById(int id) {
        return vaultItemDao.getItemById(id);
    }

    // Insert a vault item
    public void insert(VaultItem item) {
        executorService.execute(() -> vaultItemDao.insert(item));
    }

    // Update a vault item
    public void update(VaultItem item) {
        executorService.execute(() -> vaultItemDao.update(item));
    }

    // Delete a vault item
    public void delete(VaultItem item) {
        executorService.execute(() -> vaultItemDao.delete(item));
    }

    // Delete all vault items
    public void deleteAllItems() {
        executorService.execute(() -> vaultItemDao.deleteAllItems());
    }
}

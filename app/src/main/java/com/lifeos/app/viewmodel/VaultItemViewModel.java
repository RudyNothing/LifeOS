
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.VaultItem;
import com.lifeos.app.data.repository.VaultItemRepository;

import java.util.List;

public class VaultItemViewModel extends AndroidViewModel {

    private final VaultItemRepository vaultItemRepository;
    private final LiveData<List<VaultItem>> allItems;

    public VaultItemViewModel(@NonNull Application application) {
        super(application);

        vaultItemRepository = new VaultItemRepository(application);
        allItems = vaultItemRepository.getAllItems();
    }

    // Retrieve all vault items
    public LiveData<List<VaultItem>> getAllItems() {
        return allItems;
    }

    // Retrieve a vault item by its ID
    public LiveData<VaultItem> getItemById(int id) {
        return vaultItemRepository.getItemById(id);
    }

    // Insert a vault item
    public void insert(VaultItem item) {
        vaultItemRepository.insert(item);
    }

    // Update a vault item
    public void update(VaultItem item) {
        vaultItemRepository.update(item);
    }

    // Delete a vault item
    public void delete(VaultItem item) {
        vaultItemRepository.delete(item);
    }

    // Delete all vault items
    public void deleteAllItems() {
        vaultItemRepository.deleteAllItems();
    }
}

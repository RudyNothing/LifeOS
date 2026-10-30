
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.FinanceTransaction;
import com.lifeos.app.data.repository.FinanceTransactionRepository;

import java.util.List;

public class FinanceTransactionViewModel extends AndroidViewModel {

    private final FinanceTransactionRepository transactionRepository;
    private final LiveData<List<FinanceTransaction>> allTransactions;

    public FinanceTransactionViewModel(@NonNull Application application) {
        super(application);

        transactionRepository =
                new FinanceTransactionRepository(application);

        allTransactions = transactionRepository.getAllTransactions();
    }

    // Retrieve all transactions
    public LiveData<List<FinanceTransaction>> getAllTransactions() {
        return allTransactions;
    }

    // Retrieve a transaction by its ID
    public LiveData<FinanceTransaction> getTransactionById(int id) {
        return transactionRepository.getTransactionById(id);
    }

    // Insert a transaction
    public void insert(FinanceTransaction transaction) {
        transactionRepository.insert(transaction);
    }

    // Update a transaction
    public void update(FinanceTransaction transaction) {
        transactionRepository.update(transaction);
    }

    // Delete a transaction
    public void delete(FinanceTransaction transaction) {
        transactionRepository.delete(transaction);
    }

    // Delete all transactions
    public void deleteAllTransactions() {
        transactionRepository.deleteAllTransactions();
    }
}

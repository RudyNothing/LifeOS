package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.FinanceTransactionDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.FinanceTransaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceTransactionRepository{

    private final FinanceTransactionDao transactionDao;
    private final LiveData<List<FinanceTransaction>> allTransactions;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FinanceTransactionRepository(Application application){
        AppDatabase database = DatabaseProvider.getDatabase(application);
        transactionDao = database.financeTransactionDao();
        allTransactions = transactionDao.getAllTransactions();
    }

    public LiveData<List<FinanceTransaction>> getAllTransactions(){
        return allTransactions;
    }

    public LiveData<FinanceTransaction> getTransactionById(int id){
        return transactionDao.getTransactionById(id);
    }

    public void insert(FinanceTransaction transaction){
        executorService.execute(() -> transactionDao.insert(transaction));
    }

    public void update(FinanceTransaction transaction){
        executorService.execute(() -> transactionDao.update(transaction));
    }

    public void delete(FinanceTransaction transaction){
        executorService.execute(()-> transactionDao.delete(transaction));
    }
    public void deleteAllTransactions(){
        executorService.execute(
                transactionDao::deleteAllTransactions
        );
    }
}
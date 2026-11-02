package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.FinanceTransaction;

import java.util.List;

@Dao
public interface FinanceTransactionDao{
    @Insert
    void insert(FinanceTransaction transaction);

    @Update
    void update(FinanceTransaction transaction);

    @Delete
    void delete(FinanceTransaction transaction);

    @Query("SELECT * FROM finance_transactions")
    LiveData<List<FinanceTransaction>> getAllTransactions();

    @Query("SELECT * FROM finance_transactions WHERE id = :id LIMIT 1")
    LiveData<FinanceTransaction> getTransactionById(int id);

    @Query("DELETE FROM finance_transactions")
    void deleteAllTransactions();
}
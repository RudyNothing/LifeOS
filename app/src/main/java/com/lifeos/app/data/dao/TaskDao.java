package com.lifeos.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lifeos.app.data.entity.Task;

import java.util.List;

@Dao
public interface TaskDao{
    @Insert
    void insert(Task task); //To add a task

    @Update
    void update(Task task); //To modify a task

    @Delete
    void delete(Task task); // To delete a task

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks(); // To retrieve all the tasks

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    LiveData<Task> getTaskById(int id); //To retrieve one single task

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' ORDER BY dueDate ASC")
    LiveData<List<Task>> getPendingTasks(); //To retrieve unfinished tasks

    @Query("DELETE FROM tasks")
    void deleteAllTasks(); //To clear the task table
}

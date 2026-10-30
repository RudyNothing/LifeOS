package com.lifeos.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lifeos.app.data.dao.TaskDao;
import com.lifeos.app.data.database.AppDatabase;
import com.lifeos.app.data.database.DatabaseProvider;
import com.lifeos.app.data.entity.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository{
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    private final LiveData<List<Task>> pendingTasks;

    private final LiveData<List<Task>> normalTasks;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TaskRepository(Application application){
        AppDatabase database = DatabaseProvider.getDatabase(application);
        taskDao = database.taskDao();
        normalTasks = taskDao.getNormalTasks();
        allTasks = taskDao.getAllTasks();
        pendingTasks = taskDao.getPendingTasks();
    }
    public LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }
    public LiveData<List<Task>> getPendingTasks(){
        return pendingTasks;
    }
    public LiveData<Task> getTaskById(int id){
        return taskDao.getTaskById(id);
    }

    public void insert(Task task){
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task){
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task){
        executorService.execute(() -> taskDao.delete(task));
    }

    public void deleteAllTasks(){
        executorService.execute(() -> taskDao.deleteAllTasks());
    }

    public LiveData<List<Task>> getNormalTasks() { return normalTasks; }
}

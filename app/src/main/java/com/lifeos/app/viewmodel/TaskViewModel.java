
package com.lifeos.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lifeos.app.data.entity.Task;
import com.lifeos.app.data.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private final LiveData<List<Task>> allTasks;
    private final LiveData<List<Task>> pendingTasks;

    private final LiveData<List<Task>> normalTasks;
    public TaskViewModel(@NonNull Application application) {
        super(application);

        taskRepository = new TaskRepository(application);
        normalTasks = taskRepository.getNormalTasks();
        allTasks = taskRepository.getAllTasks();
        pendingTasks = taskRepository.getPendingTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getPendingTasks() {
        return pendingTasks;
    }

    public LiveData<Task> getTaskById(int id) {
        return taskRepository.getTaskById(id);
    }

    public LiveData<List<Task>> getNormalTasks() {
        return normalTasks;
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }
}

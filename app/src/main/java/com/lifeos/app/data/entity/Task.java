package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;

    private String priority;
    private String status;
    private String category;

    private long dueDate;
    private long reminderDate;

    private long createdAt;
    private long updatedAt;
    private long completedAt;

    public Task(){
    }

    public Task(String title, String description, String priority, String status, String category, long dueDate, long reminderDate, long createdAt, long updatedAt, long completedAt){
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.dueDate = dueDate;
        this.reminderDate = reminderDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getPriority(){
        return priority;
    }
    public void setPriority(String priority){
        this.priority = priority;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public long getDueDate(){
        return dueDate;
    }
    public void setDueDate(long dueDate){
        this.dueDate = dueDate;
    }
    public long getReminderDate(){
        return reminderDate;
    }
    public void setReminderDate(long reminderDate){
        this.reminderDate = reminderDate;
    }
    public long getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(long createdAt){
        this.createdAt = createdAt;
    }
    public long getUpdatedAt(){
        return updatedAt;
    }
    public void setUpdatedAt(long updatedAt){
        this.updatedAt = updatedAt;
    }
    public long getCompletedAt(){
        return completedAt;
    }
    public void setCompletedAt(long completedAt){
        this.completedAt = completedAt;
    }
}

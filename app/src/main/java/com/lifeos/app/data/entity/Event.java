package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;

    private long startTime;
    private long endTime;
    private String location;
    private boolean allDay;
    private long reminderDate;
    private String category;
    private long createdAt;
    private long updatedAt;

    public Event(){
    }
    public Event(String title, String description,long startTime, long endTime, String location, boolean allDay, long reminderDate, String category, long createdAt, long updatedAt){
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.allDay = allDay;
        this.reminderDate = reminderDate;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    public long getStartTime(){
        return startTime;
    }
    public void setStartTime(long startTime){
        this.startTime = startTime;
    }
    public long getEndTime(){
        return endTime;
    }
    public void setEndTime(long endTime){
        this.endTime = endTime;
    }
    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public boolean isAllDay(){
        return allDay;
    }
    public void setAllDay(boolean allDay){
        this.allDay = allDay;
    }
    public long getReminderDate(){
        return reminderDate;
    }
    public void setReminderDate(long reminderDate){
        this.reminderDate = reminderDate;
    }
    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
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
}
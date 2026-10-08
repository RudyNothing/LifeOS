package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_entries")
public class JournalEntry{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    private long date;
    private String mood;
    private boolean memory;
    private long createdAt;
    private long updatedAt;

    public JournalEntry(){
    }
    public JournalEntry(String title, String content, long date, String mood, boolean memory, long createdAt, long updatedAt){
        this.title = title;
        this.content = content;
        this.date = date;
        this.mood = mood;
        this.memory = memory;
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
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public long getDate(){
        return date;
    }
    public void setDate(long date){
        this.date = date;
    }
    public String getMood(){
        return mood;
    }
    public void setMood(String mood){
        this.mood = mood;
    }
    public boolean isMemory(){
        return memory;
    }
    public void setMemory(boolean memory){
        this.memory = memory;
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
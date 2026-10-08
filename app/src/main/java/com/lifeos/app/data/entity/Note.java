package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    private String category;

    private boolean pinned;

    private long createdAt;
    private long updatedAt;

    public Note(){
    }

    public Note(String title, String content, String category, boolean pinned, long createdAt, long updatedAt){
        this.title = title;
        this.content = content;
        this.category = category;
        this.pinned = pinned;
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
    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public boolean isPinned(){
        return pinned;
    }
    public void setPinned(boolean pinned){
        this.pinned = pinned;
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
package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vault_items")
public class VaultItem{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String username;
    private String encryptedPassword;
    private String encryptedNotes;
    private String category;
    private long createdAt;
    private long updatedAt;

    public VaultItem(){
    }
    public VaultItem(String title, String username, String encryptedPassword, String encryptedNotes, String category, long createdAt, long updatedAt){
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptedNotes = encryptedNotes;
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
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getEncryptedPassword(){
        return encryptedPassword;
    }
    public void setEncryptedPassword(String encryptedPassword){
        this.encryptedPassword = encryptedPassword;
    }
    public String getEncryptedNotes(){
        return encryptedNotes;
    }
    public void setEncryptedNotes(String encryptedNotes){
        this.encryptedNotes = encryptedNotes;
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
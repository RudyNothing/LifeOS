package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class Profile{
    @PrimaryKey
    private int id;

    private String name;
    private String email;
    private String profileImage;
    private String currency;

    private long createdAt;
    private long updatedAt;

    public Profile(){
    }
    public Profile( int id, String name, String email, String profileImage, String currency, long createdAt, long updatedAt){
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getProfileImage(){
        return profileImage;
    }
    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }
    public String getCurrency(){
        return currency;
    }
    public void setCurrency(String currency){
        this.currency = currency;
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

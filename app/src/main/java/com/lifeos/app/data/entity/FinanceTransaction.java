package com.lifeos.app.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "finance_transactions")
public class FinanceTransaction{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String type;
    private double amount;

    private String category;
    private String title;
    private String description;
    private long date;
    private String paymentMethod;
    private long createdAt;
    private long updatedAt;

    public FinanceTransaction(){
    }
    public FinanceTransaction(String type, double amount, String category, String title, String description, long date, String paymentMethod, long createdAt, long updatedAt){
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.title = title;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public double getAmount(){
        return amount;
    }
    public void setAmount(double amount){
        this.amount = amount;
    }
    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
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
    public long getDate(){
        return date;
    }
    public void setDate(long date){
        this.date = date;
    }
    public String getPaymentMethod(){
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
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


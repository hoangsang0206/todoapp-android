package com.example.todoapp.models;

import java.io.Serializable;

public class Todo implements Serializable {
    protected String id;
    protected String title;
    protected String description;
    protected String dateToComplete;
    protected String dateCompleted;
    protected boolean completeStatus;
    protected String categoryId;

    public Todo() {
    }

    public Todo(String id, String title, String description, String dateToComplete, String dateCompleted, boolean completeStatus, String categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateToComplete = dateToComplete;
        this.dateCompleted = dateCompleted;
        this.completeStatus = completeStatus;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateToComplete() {
        return dateToComplete;
    }

    public void setDateToComplete(String dateToComplete) {
        this.dateToComplete = dateToComplete;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public boolean isCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(boolean completeStatus) {
        this.completeStatus = completeStatus;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}

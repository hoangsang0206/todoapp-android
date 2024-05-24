package com.example.todoapp.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Todo implements Serializable {
    protected String id;
    protected String title;
    protected String description;
    protected LocalDateTime dateToComplete;
    protected boolean completeStatus;
    protected Category category;

    public Todo() {
    }

    public Todo(String id, String title, String description, LocalDateTime dateToComplete, boolean completeStatus, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateToComplete = dateToComplete;
        this.completeStatus = completeStatus;
        this.category = category;
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

    public LocalDateTime getDateToComplete() {
        return dateToComplete;
    }

    public void setDateToComplete(LocalDateTime dateToComplete) {
        this.dateToComplete = dateToComplete;
    }

    public boolean isCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(boolean completeStatus) {
        this.completeStatus = completeStatus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

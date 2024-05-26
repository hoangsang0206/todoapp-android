package com.example.todoapp.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.todoapp.BR;

public class TodoViewModel  {
    protected String title;
    protected String description;
    protected String dateToComplete;

    public TodoViewModel(String title, String description, String dateToComplete) {
        this.title = title;
        this.description = description;
        this.dateToComplete = dateToComplete;
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
}

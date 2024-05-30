package com.example.todoapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    protected String id;
    protected ArrayList<Category> categories;
    protected ArrayList<Todo> todoList;
    protected Map<String, String> settings;

    public User(String id, ArrayList<Category> categories, ArrayList<Todo> todoList, Map<String, String> settings) {
        this.id = id;
        this.categories = categories;
        this.todoList = todoList;
        this.settings = settings;
    }

    public User() {
        this.categories = new ArrayList<>();
        this.todoList = new ArrayList<>();
        this.settings = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<Todo> getTodoList() {
        return todoList;
    }

    public void setTodoList(ArrayList<Todo> todoList) {
        this.todoList = todoList;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}

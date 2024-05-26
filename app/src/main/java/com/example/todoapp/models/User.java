package com.example.todoapp.models;

import java.util.ArrayList;

public class User {
    protected String id;
    protected ArrayList<Category> categories;
    protected ArrayList<Todo> todoList;

    public User(String id, ArrayList<Category> categories, ArrayList<Todo> todoList) {
        this.id = id;
        this.categories = categories;
        this.todoList = todoList;
    }

    public User() {
        this.categories = new ArrayList<>();
        this.todoList = new ArrayList<>();
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
}

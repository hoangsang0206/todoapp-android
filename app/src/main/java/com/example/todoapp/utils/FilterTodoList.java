package com.example.todoapp.utils;

import com.example.todoapp.models.Todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FilterTodoList {
    public static ArrayList<Todo> today(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo -> ParseDateTime.toDate(todo.getDateToComplete()).isEqual(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public static ArrayList<Todo> future() {
        return new ArrayList<>();
    }

    public static ArrayList<Todo> previous() {
        return new ArrayList<>();
    }

    public static ArrayList<Todo> todayCompleted() {
        return new ArrayList<>();
    }

    public static ArrayList<Todo> previousCompleted() {
        return new ArrayList<>();
    }
}

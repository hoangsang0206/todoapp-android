package com.example.todoapp.utils;

import com.example.todoapp.models.Todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FilterTodoList {
    public static ArrayList<Todo> today(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && date.isEqual(LocalDate.now()) && !todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> future(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date == null || date.isAfter(LocalDate.now()) && !todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> previous(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    LocalDate dateCompleted = ParseDateTime.toDate(todo.getDateCompleted());
                    return date != null && date.isBefore(LocalDate.now())
                            && (dateCompleted == null || !dateCompleted.isEqual(LocalDate.now()));
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> todayCompleted(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateCompleted());
                    return date != null && date.isEqual(LocalDate.now()) && todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> previousCompleted(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateCompleted());
                    return date != null && date.isBefore(LocalDate.now()) && todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> previousNotCompleted(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && date.isBefore(LocalDate.now()) && !todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }
}

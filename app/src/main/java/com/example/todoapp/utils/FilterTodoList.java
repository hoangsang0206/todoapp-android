package com.example.todoapp.utils;

import com.example.todoapp.models.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FilterTodoList {
    public static ArrayList<Todo> byDate(ArrayList<Todo> todoList, LocalDate date) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate _date = ParseDateTime.toDate(todo.getDateToComplete());
                    return _date != null && _date.isEqual(date);
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> today(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && date.isEqual(LocalDate.now()) && !todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> day(ArrayList<Todo> todoList, java.time.DayOfWeek dayOfWeek) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo -> {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && date.getDayOfWeek() == dayOfWeek;
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> week(ArrayList<Todo> todoList) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(7);

        return (ArrayList<Todo>) todoList.stream()
                .filter(todo -> {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && (date.isEqual(startOfWeek) || (date.isAfter(startOfWeek) && date.isBefore(endOfWeek)));
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> future(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    LocalDate dateCompleted = ParseDateTime.toDate(todo.getDateCompleted());
                    return (date == null || date.isAfter(LocalDate.now()))
                            && (dateCompleted == null || !dateCompleted.isEqual(LocalDate.now()));
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

    public static ArrayList<Todo> completed(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateCompleted());
                    return date != null && todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }

    public static ArrayList<Todo> incomplete(ArrayList<Todo> todoList) {
        return (ArrayList<Todo>) todoList.stream()
                .filter(todo ->  {
                    LocalDate date = ParseDateTime.toDate(todo.getDateToComplete());
                    return date != null && !todo.isCompleteStatus();
                }).collect(Collectors.toList());
    }
}

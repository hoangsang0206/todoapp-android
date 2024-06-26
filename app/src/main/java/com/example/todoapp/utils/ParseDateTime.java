package com.example.todoapp.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ParseDateTime {
    public static String toString(LocalDateTime localDateTime, String pattern) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return formatter.format(localDateTime);
    }

    public static String toString(LocalDate localDate) {
        if(localDate == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US);
        return formatter.format(localDate);
    }

    public static String toString(LocalTime localTime) {
        if(localTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.US);
        return formatter.format(localTime);
    }

    public static String toString(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a", Locale.US);
        return formatter.format(localDateTime);
    }

    public static LocalDate toDate(String dateTime) {
        if(dateTime == null || dateTime.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a", Locale.US);
        return LocalDate.parse(dateTime, formatter);
    }

    public static LocalTime toTime(String dateTime) {
        if(dateTime == null || dateTime.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a", Locale.US);
        return LocalTime.parse(dateTime, formatter);
    }

    public static LocalTime toTime(String dateTime, String pattern) {
        if(dateTime == null || dateTime.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return LocalTime.parse(dateTime, formatter);
    }

    public static LocalDateTime fromString(String dateStr) {
        if(dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a", Locale.US);
        return LocalDateTime.parse(dateStr, formatter);
    }
}

package com.example.todoapp.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ParseDateTime {
    public static String toString(LocalDateTime localDateTime, String pattern) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(localDateTime);
    }

    public static String toString(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a");
        return formatter.format(localDateTime);
    }

    public static LocalDateTime fromString(String dateStr) {
        if(dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a");
        return LocalDateTime.parse(dateStr, formatter);
    }

}

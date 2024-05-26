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

    public static LocalDateTime parseFromString(String dateStr) {
        return LocalDateTime.now();
    }

}

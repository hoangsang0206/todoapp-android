package com.example.todoapp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.todoapp.MainActivity;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class Notification {
    public static int size = 0;
    public static void setNotification(Context context, ArrayList<Todo> todoList) {
        if(context == null || todoList == null) {
            return;
        }

        cancelAllNotify(context);
        size = 0;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(int i = 0; i < todoList.size(); i++) {
            Todo todo = todoList.get(i);
            LocalDateTime timeToNotify = ParseDateTime.fromString(todo.getTimeToNotify());
            if(timeToNotify != null && !todo.isCompleteStatus() && timeToNotify.toLocalDate().isEqual(LocalDate.now())) {
                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("title", "Công việc sắp đến hạn - " + ParseDateTime.toString(timeToNotify, "HH:mm a"));
                intent.putExtra("body", todo.getTitle());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_IMMUTABLE);

                Duration duration = Duration.between(LocalDateTime.now(), timeToNotify);
                long current = System.currentTimeMillis();
                if(duration.toMillis() >= 0) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, current + duration.toMillis(), pendingIntent);
                }

                size += 1;
            }
        }
    }

    public static void cancelAllNotify(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for(int i = 0; i < size; i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

}

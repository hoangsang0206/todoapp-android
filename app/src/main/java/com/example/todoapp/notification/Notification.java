package com.example.todoapp.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class Notification {
    private static final String CHANNE_ID = "TodoList";
    private static int size = 0;
    public static boolean canNotify = true;
    public static ArrayList<Todo> todoList;
    public static Context context;

    public static void createNotificationChannel() {
        if(context == null || !canNotify) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNE_ID, "TodoList", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel for TodoList");

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void setNotification() {
        if(context == null || todoList == null || !canNotify) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        cancelAllNotify(alarmManager);
        size = 0;

        for(int i = 0; i < todoList.size(); i++) {
            Todo todo = todoList.get(i);
            LocalDateTime timeToNotify = ParseDateTime.fromString(todo.getTimeToNotify());
            if(timeToNotify != null && !todo.isCompleteStatus()
                    && timeToNotify.toLocalDate().isEqual(LocalDate.now()) && timeToNotify.isAfter(LocalDateTime.now())) {
                String timeToComplete = ParseDateTime.toString(ParseDateTime.fromString(todo.getDateToComplete()), "HH:mm a");

                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("title", "Công việc sắp đến hạn - " + (timeToComplete != null ? timeToComplete : ""));
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

    public static void cancelAllNotify(AlarmManager alarmManager) {
        for(int i = 0; i < size; i++) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_IMMUTABLE);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    public static void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.deleteNotificationChannel(CHANNE_ID);

        cancelAllNotify(alarmManager);
        size = 0;
    }

}

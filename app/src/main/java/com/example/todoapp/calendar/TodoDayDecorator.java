package com.example.todoapp.calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.LocalDate;
import java.util.ArrayList;

public class TodoDayDecorator implements DayViewDecorator {
    public static ArrayList<Todo> todoList = new ArrayList<>();
    public static CalendarDay daySelected;

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if(daySelected != null && daySelected.equals(day)) {
            return false;
        }

        for(Todo todo : todoList) {
            LocalDate todoDate = ParseDateTime.toDate(todo.getDateToComplete());
            if(todoDate != null && !todoDate.isEqual(LocalDate.now())
                    && todoDate.isEqual(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#1877f2")));
    }
}

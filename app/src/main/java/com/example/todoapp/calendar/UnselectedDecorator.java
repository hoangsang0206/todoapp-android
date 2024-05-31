package com.example.todoapp.calendar;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class UnselectedDecorator implements DayViewDecorator {
    CalendarDay day;

    public UnselectedDecorator(CalendarDay day) {
        this.day = day;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return !day.equals(this.day)
                && !this.day.equals(CalendarDay.today())
                && this.day.getMonth() == day.getMonth();
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.BLACK));
    }
}

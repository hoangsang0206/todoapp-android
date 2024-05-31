package com.example.todoapp.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CurrentDayDecorator implements DayViewDecorator  {
    CalendarDay day;
    public CurrentDayDecorator() {
        day = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#1877f2")));
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.3f));
    }
}

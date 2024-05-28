package com.example.todoapp.adapters;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ScrollableLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollable;

    public ScrollableLinearLayoutManager(Context context, boolean isScrollable) {
        super(context);

        this.isScrollable = isScrollable;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollable;
    }
}

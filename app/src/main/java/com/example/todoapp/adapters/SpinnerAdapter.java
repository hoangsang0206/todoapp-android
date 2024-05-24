package com.example.todoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todoapp.R;
import com.example.todoapp.models.Category;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Category> {
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_selected, parent, false);
            holder = new ViewHolder();
            holder.tv_selected = convertView.findViewById(R.id.tv_spinner_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = this.getItem(position);
        if(category != null) {
            holder.tv_selected.setText(category.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
        Category category = this.getItem(position);
        if(category != null) {
            ((TextView) convertView.findViewById(R.id.tv_spinner_item)).setText(category.getName());
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView tv_selected;
    }
}

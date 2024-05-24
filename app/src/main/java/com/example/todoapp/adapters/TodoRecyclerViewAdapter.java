package com.example.todoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.todoapp.databinding.ListItemBinding;
import com.example.todoapp.models.Todo;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder> {
    private final ArrayList<Todo> todoList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public TodoRecyclerViewAdapter(ArrayList<Todo> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        if(todo == null) {
            return;
        }

        viewBinderHelper.bind(holder.binding.swiperRevealLayout, todo.getId());
        holder.binding.tvTodo.setText(todo.getTitle());
        holder.binding.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding binding;

        public TodoViewHolder(@NonNull ListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());

            binding = itemRowBinding;
        }
    }
}

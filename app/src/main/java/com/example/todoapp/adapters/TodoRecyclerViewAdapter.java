package com.example.todoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.todoapp.R;
import com.example.todoapp.UpdateTodoActivity;
import com.example.todoapp.databinding.ListItemBinding;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.TodoViewHolder> {
    private ArrayList<Todo> todoList;
    private Context context;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public TodoRecyclerViewAdapter(Context context, ArrayList<Todo> todoList) {
        this.context = context;
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
        holder.binding.tvTime.setText(todo.getDateToComplete() != null ? todo.getDateToComplete() : "No time");

        if(todo.isCompleteStatus()) {
            holder.binding.ckboxComplete.setChecked(true);
            holder.binding.tvTodo.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.tvTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.listItemMain.setBackground(
                    ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.bg_list_item_completed));
        }

        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/todoList/" + todo.getId());
                ref.removeValue();
            }
        });
        holder.binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateTodoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Todo", todo);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.binding.ckboxComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref_status = db.getReference("users/" + user.getUid() + "/todoList/" + todo.getId() + "/completeStatus");
                DatabaseReference ref_date = db.getReference("users/" + user.getUid() + "/todoList/" + todo.getId() + "/dateCompleted");
                ref_status.setValue(isChecked);
                ref_date.setValue(isChecked ? ParseDateTime.toString(LocalDateTime.now()) : null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    public void setTodoList(ArrayList<Todo> newTodoList) {
        this.todoList = newTodoList;
        notifyDataSetChanged();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding binding;

        public TodoViewHolder(@NonNull ListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());

            binding = itemRowBinding;
        }
    }
}

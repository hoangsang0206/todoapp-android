package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.todoapp.databinding.ActivityUpdateTodoBinding;
import com.example.todoapp.models.Todo;
import com.example.todoapp.models.TodoViewModel;

public class UpdateTodoActivity extends AppCompatActivity {
    ActivityUpdateTodoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_bx_arrow_back);

        Intent intent = getIntent();
        Todo todo = (Todo) intent.getSerializableExtra("Todo");

        TodoViewModel todoViewModel = new TodoViewModel(todo.getTitle(), todo.getDescription(), todo.getDateToComplete());
        binding.setTodo(todoViewModel);
    }
}
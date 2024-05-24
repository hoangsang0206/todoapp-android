package com.example.todoapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todoapp.R;
import com.example.todoapp.adapters.TodoRecyclerViewAdapter;
import com.example.todoapp.databinding.FragmentTodoBinding;
import com.example.todoapp.models.Todo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TodoFragment extends Fragment {
    private ArrayList<Button> categoryButtons;
    private ArrayList<Todo> todoList;
    private FragmentTodoBinding binding;

    public TodoFragment() {
        // Required empty public constructor
    }

    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoBinding.inflate(inflater, container, false);
        categoryButtons = new ArrayList<>();

        categoryButtons.add(binding.cate1);
        categoryButtons.add(binding.cate2);
        categoryButtons.add(binding.cate3);

        categoryButtons.forEach(button -> {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryButtons.forEach(button1 -> {
                        button1.setActivated(false);
                        button1.setTextColor(Color.parseColor("#676d74"));
                    });

                    button.setActivated(true);
                    button.setTextColor(Color.parseColor("#FFFFFF"));
                }
            });
        });

        todoList = loadTodoList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        binding.todoRecyclerView.setLayoutManager(linearLayoutManager);
        binding.todoRecyclerView.setAdapter(new TodoRecyclerViewAdapter(todoList));

        return binding.getRoot();
    }

    private ArrayList<Todo> loadTodoList() {
        ArrayList<Todo> usrTodoList = new ArrayList<>();
        usrTodoList.add(new Todo("1", "Todo 1111111111111111111111111111111111111111111111111111111111111", "", LocalDateTime.now(), false, null));
        usrTodoList.add(new Todo("2", "Todo 2", "", LocalDateTime.now(), false, null));
        usrTodoList.add(new Todo("3", "Todo 3", "", LocalDateTime.now(), false, null));
        usrTodoList.add(new Todo("4", "Todo 4", "", LocalDateTime.now(), false, null));

        return usrTodoList;
    }
}
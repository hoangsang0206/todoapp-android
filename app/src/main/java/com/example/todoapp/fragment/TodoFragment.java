package com.example.todoapp.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todoapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {
    ArrayList<Button> categoryButtons;

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
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        categoryButtons = new ArrayList<>();

        categoryButtons.add(view.findViewById(R.id.cate1));
        categoryButtons.add(view.findViewById(R.id.cate2));
        categoryButtons.add(view.findViewById(R.id.cate3));

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
        return view;
    }
}
package com.example.todoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todoapp.adapters.TodoRecyclerViewAdapter;
import com.example.todoapp.calendar.CurrentDayDecorator;
import com.example.todoapp.calendar.SelectedDecorator;
import com.example.todoapp.calendar.TodoDayDecorator;
import com.example.todoapp.calendar.UnselectedDecorator;
import com.example.todoapp.databinding.FragmentCalendarBinding;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.FilterTodoList;
import com.example.todoapp.utils.ParseDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class CalendarFragment extends Fragment {
    FragmentCalendarBinding binding;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.todoRcview.setLayoutManager(layoutManager);
        loadTodoList(LocalDate.now());

        binding.calendar.setTileHeightDp(50);
        binding.calendar.setDynamicHeightEnabled(false);
        binding.calendar.addDecorator(new CurrentDayDecorator());
        binding.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
               if(selected) {
                   LocalDate _date = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
                   TodoDayDecorator.daySelected = date;
                   loadTodoList(_date);
                   updateDecorator(date);
               }
            }
        });

        return binding.getRoot();
    }

    private void updateDecorator(CalendarDay day) {
        binding.calendar.removeDecorators();
        binding.calendar.addDecorator(new UnselectedDecorator(day));
        binding.calendar.addDecorator(new TodoDayDecorator());
        binding.calendar.addDecorator(new SelectedDecorator(day));
        if(!day.equals(CalendarDay.today())) {
            binding.calendar.addDecorator(new CurrentDayDecorator());
        }
    }

    private void loadTodoList(LocalDate date) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        binding.todoRcview.setVisibility(View.INVISIBLE);
        binding.todoShimmer.setVisibility(View.VISIBLE);
        binding.todoShimmer.startShimmer();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/todoList");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ArrayList<Todo> todoList = new ArrayList<>();
                    snapshot.getChildren().forEach(snap -> {
                        todoList.add(snap.getValue(Todo.class));
                    });

                    ArrayList<Todo> todayTodoList = FilterTodoList.byDate(todoList, date);
                    todayTodoList.sort(Comparator.comparing(t -> {
                        LocalDateTime dateTime =  ParseDateTime.fromString(t.getDateToComplete());
                        return dateTime != null ? dateTime : LocalDateTime.MAX;
                    }));

                    binding.todoRcview.setAdapter(new TodoRecyclerViewAdapter(getContext(), todayTodoList));
                    TodoDayDecorator.todoList = todoList;
                    binding.calendar.addDecorator(new TodoDayDecorator());
                } else {
                    TodoDayDecorator.todoList = new ArrayList<>();
                    binding.calendar.removeDecorator(new TodoDayDecorator());
                    binding.todoRcview.setAdapter(null);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.todoRcview.setVisibility(View.VISIBLE);
                        binding.todoShimmer.setVisibility(View.INVISIBLE);
                        binding.todoShimmer.stopShimmer();
                    }
                }, 1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
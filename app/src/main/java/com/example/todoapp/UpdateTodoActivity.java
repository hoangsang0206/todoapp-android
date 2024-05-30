package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.todoapp.adapters.SpinnerAdapter;
import com.example.todoapp.databinding.ActivityUpdateTodoBinding;
import com.example.todoapp.databinding.PopupCreateTodoBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class UpdateTodoActivity extends AppCompatActivity {
    ActivityUpdateTodoBinding binding;
    Todo todo;

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
        todo = (Todo) intent.getSerializableExtra("Todo");
        if(todo == null) {
            return;
        }

        getTodo();
        getCategories();
        onClickListenerUpdateTodo();
    }

    private void getTodo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("todoList").child(todo.getId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todo = snapshot.getValue(Todo.class);
                if(todo != null) {
                    binding.txtTodo.setText(todo.getTitle());
                    binding.txtDescription.setText(todo.getDescription());
                    binding.changeDate.setText(todo.getDateToComplete() != null ?
                            ParseDateTime.toString(ParseDateTime.fromString(todo.getDateToComplete()), "dd/MM/yyyy") : "No date");
                    binding.changeTime.setText(todo.getDateToComplete() != null ?
                            ParseDateTime.toString(ParseDateTime.fromString(todo.getDateToComplete()), "HH:mm a") : "No time");
                    binding.changeTimeNotify.setText(todo.getTimeToNotify() != null ?  ParseDateTime.toString(ParseDateTime.fromString(todo.getTimeToNotify()), "HH:mm a") : "No time");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getCategories() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> categories = new ArrayList<>();
                categories.add(new Category("none", "Không có danh mục"));
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Category category = dataSnapshot.getValue(Category.class);
                        categories.add(category);
                    }
                }

                binding.cagoriesSpinner.setAdapter(new SpinnerAdapter(UpdateTodoActivity.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
                for(int i = 0; i < categories.size(); i++) {
                    if(categories.get(i).getId().equals(todo.getCategoryId())) {
                        binding.cagoriesSpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void onClickListenerUpdateTodo() {
        binding.txtTodo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    todo.setTitle(s.toString());
                    updateTodo(todo);
                }
            }
        });
        binding.txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                todo.setDescription(s.toString());
                updateTodo(todo);
            }
        });
        binding.cagoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position >= 0) {
                    Category category = (Category) parent.getItemAtPosition(position);
                    if(category == null) {
                        return;
                    }

                    if (category.getId().equals("none")) {
                        todo.setCategoryId(null);
                    } else {
                        todo.setCategoryId(category.getId());
                    }

                    updateTodo(todo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        binding.changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(ParseDateTime.toDate(todo.getDateToComplete()));
            }
        });
        binding.changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(ParseDateTime.toTime(todo.getDateToComplete()), 0);
            }
        });
        binding.changeTimeNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(ParseDateTime.toTime(todo.getTimeToNotify()), 1);
            }
        });
    }

    private void showDatePicker(LocalDate date) {
        if(date == null) {
            date = LocalDate.now();
        }
        new DatePickerDialog(UpdateTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateToComplete = ParseDateTime.toString(LocalDate.of(year, month + 1, dayOfMonth));
                String oldTime = ParseDateTime.toString(ParseDateTime.fromString(todo.getDateToComplete()),"HH:mm a");
                if(oldTime == null) {
                    oldTime = ParseDateTime.toString(LocalDateTime.now(), "HH:mm a");
                }

                todo.setDateToComplete(dateToComplete + " " + oldTime);
                updateTodo(todo);
            }
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
    }

    private void showTimePicker(LocalTime time, int type) {
        if(time == null) {
            time = LocalTime.now().plusMinutes(5);
        }
        new TimePickerDialog(UpdateTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeStr = ParseDateTime.toString(LocalTime.of(hourOfDay, minute));
                String oldDate = ParseDateTime.toString(ParseDateTime.fromString(todo.getDateToComplete()), "dd/MM/yyyy");
                if(oldDate == null) {
                    oldDate = ParseDateTime.toString(LocalDateTime.now(), "dd/MM/yyyy");
                }

                if(type == 0) {
                    todo.setDateToComplete(oldDate + " " + timeStr);
                } else {
                    todo.setTimeToNotify(oldDate + " " + timeStr);
                }

                updateTodo(todo);
            }
        }, time.getHour(), time.getMinute(), true).show();
    }

    private void updateTodo(Todo todo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid()).child("todoList").child(todo.getId());
        ref.setValue(todo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
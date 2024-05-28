package com.example.todoapp.popups;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.adapters.SpinnerAdapter;
import com.example.todoapp.databinding.PopupCreateTodoBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;
import com.example.todoapp.utils.RandomString;
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

import eightbitlab.com.blurview.RenderScriptBlur;

public class CreateTodoPopup {
    public static void showPopup(Context context) {
        PopupCreateTodoBinding binding = PopupCreateTodoBinding.inflate(LayoutInflater.from(context));
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(),
                ConstraintLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        float radius = 1f;
        View decorView = ((Activity) context).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        binding.blurView.setupWith(rootView, new RenderScriptBlur(binding.getRoot().getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);

        popupWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);
        popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        initCreateTodoPopup(binding, popupWindow, context);
        getCategories(binding, context);
    }

    private static void initCreateTodoPopup(PopupCreateTodoBinding binding, PopupWindow popup, Context context) {
        binding.blurView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        binding.blurViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        binding.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCategoryPopup.showPopup(context);
            }
        });

        final LocalDate[] dateToComplete = new LocalDate[1];
        final LocalDateTime[] dateTimeToComplete = new LocalDateTime[1];
        dateToComplete[0] = LocalDate.now();

        DatePickerDialog d_dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateToComplete[0] = LocalDate.of(year, month + 1, dayOfMonth);
                dateTimeToComplete[0] = LocalDateTime.of(dateToComplete[0].getYear(),
                        dateToComplete[0].getMonthValue(), dateToComplete[0].getDayOfMonth(), LocalTime.now().getHour(), LocalTime.now().getMinute());
            }
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
        TimePickerDialog t_dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTimeToComplete[0] = LocalDateTime.of(dateToComplete[0].getYear(),
                        dateToComplete[0].getMonthValue(), dateToComplete[0].getDayOfMonth(), hourOfDay, minute);
            }
        }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), true);

        binding.btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d_dialog.show();
            }
        });
        binding.btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t_dialog.show();
            }
        });
        binding.btnCreateTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.txtTodo.getText().toString();
                if(name == null || name.isEmpty()) {
                    binding.txtTodo.setError("Nhập tên công việc");
                } else {
                    Category category = (Category) binding.categorySpinner.getSelectedItem();
                    String id = category.getId();
                    createTodo(name, !id.equals("none") ? id : null, dateTimeToComplete[0]);
                    popup.dismiss();
                }
            }
        });
    }

    private static void createTodo(String name, String categoryId, LocalDateTime dateToComplete) {
        String id = "td_" + RandomString.random(10);
        String dateToCompleteStr = ParseDateTime.toString(dateToComplete, "dd/MM/yyyy HH:mm a");
        Todo todo = new Todo(id, name, null, dateToCompleteStr, false, categoryId);
        todo.setTimeToNotify(dateToCompleteStr);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/todoList/" + id);
        ref.setValue(todo);
    }

    private static void getCategories(PopupCreateTodoBinding binding, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> categories = new ArrayList<>();
                categories.add(new Category("none", "Không có danh mục"));
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Category category = dataSnapshot.getValue(Category.class);
                        categories.add(category);
                    }
                }

                binding.categorySpinner.setAdapter(null);
                binding.categorySpinner.setAdapter(new SpinnerAdapter(context,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}

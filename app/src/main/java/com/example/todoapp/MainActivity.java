package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.example.todoapp.adapters.SpinnerAdapter;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.databinding.PopupCreateCategoryBinding;
import com.example.todoapp.databinding.PopupCreateTodoBinding;
import com.example.todoapp.fragments.AccountFragment;
import com.example.todoapp.fragments.CalendarFragment;
import com.example.todoapp.fragments.TodoFragment;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.ParseDateTime;
import com.example.todoapp.utils.RandomString;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        placeFragment(new TodoFragment());

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_nav) {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if(id == R.id.menu_todo) {
                    placeFragment(new TodoFragment());
                } else if(id == R.id.menu_calendar) {
                    placeFragment(new CalendarFragment());
                } else if(id == R.id.menu_account) {
//                    placeFragment(new AccountFragment());
                    logout();
                }

                return false;
            }
        });

        binding.btnAddFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTodoPopup();
            }
        });
    }

    private void placeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.frameLayout.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showCreateTodoPopup() {
        PopupCreateTodoBinding createTodoBinding = PopupCreateTodoBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(createTodoBinding.getRoot(),
                ConstraintLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        float radius = 1f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        createTodoBinding.blurView.setupWith(rootView, new RenderScriptBlur(createTodoBinding.getRoot().getContext()))
                        .setFrameClearDrawable(windowBackground)
                        .setBlurRadius(radius)
                        .setBlurAutoUpdate(true);

        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
        popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in));
        initCreateTodoPopup(createTodoBinding, popupWindow);
        getCategories(createTodoBinding);
    }

    private void initCreateTodoPopup(PopupCreateTodoBinding binding, PopupWindow popup) {
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
                showCreateCategoryPopup();
            }
        });

        final LocalDate[] dateToComplete = new LocalDate[1];
        final LocalDateTime[] dateTimeToComplete = new LocalDateTime[1];

        DatePickerDialog d_dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateToComplete[0] = LocalDate.of(year, month + 1, dayOfMonth);
            }
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
        TimePickerDialog t_dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(dateToComplete[0] != null) {
                    dateTimeToComplete[0] = LocalDateTime.of(dateToComplete[0].getYear(),
                            dateToComplete[0].getMonthValue(), dateToComplete[0].getDayOfMonth(), hourOfDay, minute);
                } else {
                    dateTimeToComplete[0] = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(),
                            LocalDateTime.now().getDayOfMonth(), hourOfDay, minute);
                }
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
                    createTodo(name, category, dateTimeToComplete[0]);
                    popup.dismiss();
                }
            }
        });
    }

    private void showCreateCategoryPopup() {
        PopupCreateCategoryBinding createCategoryBinding = PopupCreateCategoryBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(createCategoryBinding.getRoot(),
                ConstraintLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        float radius = 1f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        createCategoryBinding.blurView.setupWith(rootView, new RenderScriptBlur(createCategoryBinding.getRoot().getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);

        popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in));
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        initCreateCategoryPopup(createCategoryBinding, popupWindow);
    }

    private void initCreateCategoryPopup(PopupCreateCategoryBinding binding, PopupWindow popup) {
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
        binding.btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.categoryName.getText().toString();
                if(name == null || name.isEmpty()) {
                    binding.categoryName.setError("Nhập tên danh mục");
                } else {
                    createCategory(name);
                    binding.categoryName.setText("");
                    popup.dismiss();
                }
            }
        });
        binding.btnCancelCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.categoryName.setText("");
                popup.dismiss();
            }
        });
    }

    private void createCategory(String cateName) {
        String cateId = "c_" + RandomString.random(10);
        Category category = new Category(cateId, cateName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/categories/" + cateId);
        ref.setValue(category);
    }

    private void getCategories(PopupCreateTodoBinding binding) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> categories = new ArrayList<>();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Category category = dataSnapshot.getValue(Category.class);
                        categories.add(category);
                    }
                }

                binding.categorySpinner.setAdapter(null);
                binding.categorySpinner.setAdapter(new SpinnerAdapter(MainActivity.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void createTodo(String name, Category category, LocalDateTime dateToComplete) {
        String id = "td_" + RandomString.random(10);
        Todo todo = new Todo(id, name, null, ParseDateTime.toString(dateToComplete, "dd/MM/yyyy HH:mm:a"), false, category);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/todoList/" + id);
        ref.setValue(todo);
    }

    private void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginOrRegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
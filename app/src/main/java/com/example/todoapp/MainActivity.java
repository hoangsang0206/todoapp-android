package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.todoapp.adapters.TodoRecyclerViewAdapter;
import com.example.todoapp.calendar.TodoDayDecorator;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.databinding.FragmentCalendarBinding;
import com.example.todoapp.databinding.FragmentTodoBinding;
import com.example.todoapp.databinding.NavigationHeaderBinding;
import com.example.todoapp.fragments.DasboardFragment;
import com.example.todoapp.fragments.CalendarFragment;
import com.example.todoapp.fragments.CategoriesFragment;
import com.example.todoapp.fragments.SettingFragment;
import com.example.todoapp.fragments.TodoFragment;
import com.example.todoapp.models.Todo;
import com.example.todoapp.notification.Notification;
import com.example.todoapp.popups.CreateTodoPopup;
import com.example.todoapp.utils.FilterTodoList;
import com.example.todoapp.utils.ParseDateTime;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static MainActivity instance;
    SettingFragment settingFragment;
    TodoFragment todoFragment;
    CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Notification.context = MainActivity.this;
        Notification.canNotify = getNotificationSetting();
        Notification.createNotificationChannel();

        placeFragment(new TodoFragment());
        binding.bottomNav.setSelectedItemId(R.id.menu_todo);
        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                setBottomNavCheckable(true);
                if (id == R.id.menu_nav) {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                    return false;
                } else if(id == R.id.menu_todo) {
                    todoFragment = new TodoFragment();
                    placeFragment(todoFragment);
                    return true;
                } else if(id == R.id.menu_calendar) {
                    calendarFragment = new CalendarFragment();
                    placeFragment(calendarFragment);
                    return true;
                } else if(id == R.id.menu_dashboard) {
                    placeFragment(new DasboardFragment());
                    return true;
                } else if(id == R.id.menu_setting) {
                    settingFragment = new SettingFragment();
                    placeFragment(settingFragment);
                    return true;
                }

                return false;
            }
        });
        binding.navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.menu_todo) {
                    placeFragment(new TodoFragment());
                    binding.bottomNav.setSelectedItemId(R.id.menu_todo);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if(id == R.id.menu_categories) {
                    placeFragment(new CategoriesFragment());
                    setBottomNavCheckable(false);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if(id == R.id.menu_calendar) {
                    placeFragment(new CalendarFragment());
                    binding.bottomNav.setSelectedItemId(R.id.menu_calendar);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if(id == R.id.menu_dashboard) {
                    placeFragment(new DasboardFragment());
                    binding.bottomNav.setSelectedItemId(R.id.menu_dashboard);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if(id == R.id.menu_setting) {
                    placeFragment(new SettingFragment());
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }else if(id == R.id.menu_logout) {
                    logout();
                }

                return false;
            }
        });
        binding.btnAddFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTodoPopup.showPopup(MainActivity.this);
            }
        });

        showUserInfomation();
        getTodoList();
    }

    private boolean getNotificationSetting() {
        final boolean[] result = {true};
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/")
                .child(user.getUid()).child("settings").child("notification");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    result[0] = snapshot.getValue(Boolean.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        return result[0];
    }

    private void setBottomNavCheckable(boolean checkable) {
        for(int i = 0; i < binding.bottomNav.getMenu().size(); i++) {
            binding.bottomNav.getMenu().getItem(i).setCheckable(checkable);
        }
    }

    private void placeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.frameLayout.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        View headerView = binding.navigation.getHeaderView(0);
        NavigationHeaderBinding navHeaderBinding = NavigationHeaderBinding.bind(headerView);
        navHeaderBinding.tvUserFullname.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
        navHeaderBinding.tvEmail.setText(user.getEmail());
        Glide.with(navHeaderBinding.getRoot().getContext()).load(user.getPhotoUrl()).error(R.drawable.user_no_image).into(navHeaderBinding.userImage);

        if(settingFragment != null) {
            settingFragment.showUserInfomation();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        GoogleSignInClient gsc = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        gsc.signOut();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginOrRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void getTodoList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("users/" + user.getUid() + "/todoList");

        showTodoShimmer();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Todo> todoList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Todo todo = dataSnapshot.getValue(Todo.class);
                    todoList.add(todo);
                }

                todoList.sort(Comparator.comparing(t -> {
                    LocalDateTime dateTime =  ParseDateTime.fromString(t.getDateToComplete());
                    return dateTime != null ? dateTime : LocalDateTime.MAX;
                }));

                Notification.todoList = todoList;
                Notification.setNotification();

                showTodoFragmentData(todoList);
                showCalendarFragmentData(todoList);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideTodoShimmer();
                    }
                }, 1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showTodoShimmer() {
        if(todoFragment == null) {
            return;
        }

        FragmentTodoBinding todoBinding = todoFragment.getBinding();
        todoBinding.todayTodo.setVisibility(View.GONE);
        todoBinding.todayCompleted.setVisibility(View.GONE);
        todoBinding.futureTodo.setVisibility(View.GONE);
        todoBinding.previousTodo.setVisibility(View.GONE);
        todoBinding.todoShimmer.setVisibility(View.VISIBLE);
        todoBinding.todoEmpty.setVisibility(View.GONE);
        todoBinding.todoShimmer.startShimmer();
    }

    private void hideTodoShimmer() {
        if(todoFragment == null) {
            return;
        }
        FragmentTodoBinding todoBinding = todoFragment.getBinding();
        todoBinding.todoShimmer.stopShimmer();
        todoBinding.todoShimmer.setVisibility(View.INVISIBLE);
    }

    private void showTodoFragmentData(ArrayList<Todo> todoList) {
        if(todoFragment == null) {
            return;
        }
        FragmentTodoBinding todoBinding = todoFragment.getBinding();

        if(todoList.size() == 0) {
            todoBinding.todayTodo.setVisibility(View.GONE);
            todoBinding.todayCompleted.setVisibility(View.GONE);
            todoBinding.futureTodo.setVisibility(View.GONE);
            todoBinding.previousTodo.setVisibility(View.GONE);
            todoBinding.todoEmpty.setVisibility(View.VISIBLE);
        } else {
            ArrayList<Todo> today = FilterTodoList.today(todoList);
            ArrayList<Todo> todayCompleted = FilterTodoList.todayCompleted(todoList);
            ArrayList<Todo> future = FilterTodoList.future(todoList);
            ArrayList<Todo> previous = FilterTodoList.previous(todoList);

            if(today.size() > 0) {
                todoBinding.todayTodo.setVisibility(View.VISIBLE);
                todoBinding.todayRcv.setAdapter(new TodoRecyclerViewAdapter(todoBinding.getRoot().getContext(), today));
            } else {
                todoBinding.todayTodo.setVisibility(View.GONE);
            }
            if(todayCompleted.size() > 0) {
                todoBinding.todayCompleted.setVisibility(View.VISIBLE);
                todoBinding.todayCompletedRcv.setAdapter(new TodoRecyclerViewAdapter(todoBinding.getRoot().getContext(), todayCompleted));
            } else {
                todoBinding.todayCompleted.setVisibility(View.GONE);
            }
            if(future.size() > 0) {
                todoBinding.futureTodo.setVisibility(View.VISIBLE);
                todoBinding.futureRcv.setAdapter(new TodoRecyclerViewAdapter(todoBinding.getRoot().getContext(), future));
            } else {
                todoBinding.futureTodo.setVisibility(View.GONE);
            }
            if(previous.size() > 0) {
                todoBinding.previousTodo.setVisibility(View.VISIBLE);
                todoBinding.previousRcv.setAdapter(new TodoRecyclerViewAdapter(todoBinding.getRoot().getContext(), previous));
            } else {
                todoBinding.previousTodo.setVisibility(View.GONE);
            }

            todoBinding.todoEmpty.setVisibility(View.GONE);
        }
    }

    private void showCalendarFragmentData(ArrayList<Todo> todoList) {
        if(calendarFragment == null) {
            return;
        }
        FragmentCalendarBinding calendarBinding = calendarFragment.getBinding();

        if(todoList.size() > 0) {
            LocalDate selectedDate = calendarFragment.getSelectedDate();
            if(selectedDate != null) {
                ArrayList<Todo> filteredTodoList = FilterTodoList.byDate(todoList, selectedDate);
                calendarBinding.todoRcview.setAdapter(new TodoRecyclerViewAdapter(calendarBinding.getRoot().getContext(), filteredTodoList));
            }
            TodoDayDecorator.todoList = todoList;
            calendarBinding.calendar.addDecorator(new TodoDayDecorator());
        } else {
            TodoDayDecorator.todoList = new ArrayList<>();
            calendarBinding.calendar.removeDecorator(new TodoDayDecorator());
            calendarBinding.todoRcview.setAdapter(null);
        }
    }
}
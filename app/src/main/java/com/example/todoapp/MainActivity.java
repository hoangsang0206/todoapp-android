package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.example.todoapp.adapters.SpinnerAdapter;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.databinding.NavigationHeaderBinding;
import com.example.todoapp.databinding.PopupCreateCategoryBinding;
import com.example.todoapp.databinding.PopupCreateTodoBinding;
import com.example.todoapp.fragments.AccountFragment;
import com.example.todoapp.fragments.CalendarFragment;
import com.example.todoapp.fragments.CategoriesFragment;
import com.example.todoapp.fragments.TodoFragment;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.notification.NotificationReceiver;
import com.example.todoapp.popups.CreateCategoryPopup;
import com.example.todoapp.popups.CreateTodoPopup;
import com.example.todoapp.utils.ParseDateTime;
import com.example.todoapp.utils.RandomString;
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
import java.time.ZoneOffset;
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

        createNotificationChannel();

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
                    placeFragment(new TodoFragment());
                    return true;
                } else if(id == R.id.menu_calendar) {
                    placeFragment(new CalendarFragment());
                    return true;
                } else if(id == R.id.menu_account) {
                    placeFragment(new AccountFragment());
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
                } else if(id == R.id.menu_account) {
                    placeFragment(new AccountFragment());
                    binding.bottomNav.setSelectedItemId(R.id.menu_account);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if(id == R.id.menu_setting) {
                    setBottomNavCheckable(false);
                    //
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

    private void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        View headerView = binding.navigation.getHeaderView(0);
        NavigationHeaderBinding navHeaderBinding = NavigationHeaderBinding.bind(headerView);
        navHeaderBinding.tvUserFullname.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "-------");
        navHeaderBinding.tvEmail.setText(user.getEmail());
        Glide.with(navHeaderBinding.getRoot().getContext()).load(user.getPhotoUrl()).error(R.drawable.user_no_image).into(navHeaderBinding.userImage);
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
                    finish();
                }
            }
        });
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("TodoList", "TodoList", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel for TodoList");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
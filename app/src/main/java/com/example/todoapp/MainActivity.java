package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.example.todoapp.adapters.SpinnerAdapter;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.databinding.PopupCreateCategoryBinding;
import com.example.todoapp.databinding.PopupCreateTodoBinding;
import com.example.todoapp.fragments.AccountFragment;
import com.example.todoapp.fragments.CalendarFragment;
import com.example.todoapp.fragments.TodoFragment;
import com.example.todoapp.models.Category;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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
                    placeFragment(new AccountFragment());
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
        binding.categorySpinner.setAdapter(new SpinnerAdapter(MainActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getCategories()));
        binding.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateCategoryPopup();
            }
        });
    }

    private List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Category 1 1111111111111111111111111111111111111111"));
        categories.add(new Category("2", "Category 2"));
        categories.add(new Category("3", "Category 3"));
        categories.add(new Category("4", "Category 4"));
        return categories;
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
        createCategoryBinding.blurView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        createCategoryBinding.blurViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
    }

    private void pushData(String str) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test");

        myRef.setValue(str);
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
//                txtView.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if(name != null && !name.isEmpty()) {
            binding.bottomNav.getMenu().findItem(R.id.menu_account).setTitle(name);
        }
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
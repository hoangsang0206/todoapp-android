package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.todoapp.fragment.TodoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private BottomNavigationView bottomNav;
    private FloatingActionButton btnAddFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElements();
        placeFragment(new TodoFragment());
        showUserInfomation();

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_nav) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if(id == R.id.menu_todo) {
                    if(isCurrentFragment(TodoFragment.class)) {
                        Toast.makeText(MainActivity.this, "Todo is current fragment", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Todo isn't current fragment", Toast.LENGTH_SHORT).show();
                    }
                } else if(id == R.id.menu_calendar) {

                } else if(id == R.id.menu_account) {
                    logout();
                }

                return false;
            }
        });
    }

    private void initElements() {
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setBackground(null);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.navigation);
        btnAddFloating = findViewById(R.id.btnAddFloating);
    }

    private void placeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private boolean isCurrentFragment(Class<? extends Fragment> fragmentClass) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        return currentFragment != null && fragmentClass.isInstance(currentFragment);
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
            bottomNav.getMenu().findItem(R.id.menu_account).setTitle(name);
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
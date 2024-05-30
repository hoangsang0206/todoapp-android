package com.example.todoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todoapp.LoginOrRegisterActivity;
import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.databinding.FragmentSettingBinding;
import com.example.todoapp.databinding.NavigationHeaderBinding;
import com.example.todoapp.notification.Notification;
import com.example.todoapp.popups.ChangePasswordPopup;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.PropertyPermission;


public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        showUserInfomation();
        initUI();
        updateNotification();
        return binding.getRoot();
    }

    private void updateNotification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/")
                .child(user.getUid()).child("settings").child("notification");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    boolean result = snapshot.getValue(Boolean.class);
                    if(result) {
                        if(!Notification.canNotify) {
                            Notification.canNotify = true;
                            Notification.createNotificationChannel();
                            Notification.setNotification();
                        }
                        Notification.canNotify = true;
                    } else {
                        Notification.cancelNotification();
                        Notification.canNotify = false;
                    }
                    binding.notificationSwitch.setChecked(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        Glide.with(binding.userImg).load(user.getPhotoUrl()).error(R.drawable.user_no_image).into(binding.userImg);
        binding.userFullname.setText(user.getDisplayName() != null && user.getDisplayName().isEmpty() ? "........" : user.getDisplayName());
        binding.userEmail.setText(user.getEmail());
    }

    private void initUI() {
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordPopup.showPopup(getContext());
            }
        });
        binding.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/")
                        .child(user.getUid()).child("settings").child("notification");
                ref.setValue(isChecked);
            }
        });
        binding.termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Chức năng này đang cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        MainActivity mainActivity = (MainActivity) getActivity();
        Context context = mainActivity.getApplicationContext();

        if(context == null) {
            Toast.makeText(getContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        GoogleSignInClient gsc = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN);
        gsc.signOut();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    Intent intent = new Intent(context, LoginOrRegisterActivity.class);
                    startActivity(intent);
                    mainActivity.finish();
                }
            }
        });
    }
}
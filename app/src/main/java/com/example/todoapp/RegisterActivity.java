package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.dialogs.LoadingDialog;
import com.example.todoapp.models.Category;
import com.example.todoapp.utils.EmailValidation;
import com.example.todoapp.utils.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toLogin;
    EditText txtEmail, txtPassword, txtConfirmPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_bx_arrow_back);

        toLogin = findViewById(R.id.toLogin);
        txtEmail = findViewById(R.id.register_email);
        txtPassword = findViewById(R.id.register_password);
        txtConfirmPassword = findViewById(R.id.register_confirm_password);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth();
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void firebaseAuth() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if(!validateData(email, password, confirmPassword)) {
            return;
        }
        LoadingDialog dialog = new LoadingDialog(RegisterActivity.this);
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    createUserCategories();
                    signIn(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
    }

    private void signIn(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateData(String email, String password, String confirmPassword) {
        if(email.isEmpty()) {
            txtEmail.requestFocus();
            txtEmail.setError("Email không được để trống");
            return false;
        }

        if(!EmailValidation.validateEmail(email)) {
            txtEmail.requestFocus();
            txtEmail.setError("Email không hợp lệ");
            return false;
        }

        if(password.isEmpty()) {
            txtPassword.requestFocus();
            txtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if(!confirmPassword.equals(password)) {
            txtConfirmPassword.requestFocus();
            txtConfirmPassword.setError("Xác nhận mật khẩu không đúng");
            return false;
        }

        return true;
    }


    private void createUserCategories() { //create object user in FirebaseDatabase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String id_1 = "c_" + RandomString.random(10);
        database.getReference("users/" + user.getUid() + "/categories/" + id_1).setValue(new Category(id_1, "Công việc"));
        String id_2 = "c_" + RandomString.random(10);
        database.getReference("users/" + user.getUid() + "/categories/" + id_2).setValue(new Category(id_2, "Học tập"));
        String id_3 = "c_" + RandomString.random(10);
        database.getReference("users/" + user.getUid() + "/categories/" + id_3).setValue(new Category(id_3, "Giải trí"));
    }
}
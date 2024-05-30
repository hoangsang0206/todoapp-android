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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.dialogs.LoadingDialog;
import com.example.todoapp.models.Category;
import com.example.todoapp.utils.EmailValidation;
import com.example.todoapp.utils.RandomString;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 1111;
    LoadingDialog dialog = new LoadingDialog(RegisterActivity.this);
    private CallbackManager callbackManager;
    Toolbar toolbar;
    TextView toLogin;
    EditText txtEmail, txtPassword, txtConfirmPassword;
    Button btnRegister;
    ImageView googleLogin, facebookLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        setContentView(R.layout.activity_register);

        callbackManager = CallbackManager.Factory.create();

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
        googleLogin = findViewById(R.id.btn_login_google);
        facebookLogin = findViewById(R.id.btn_login_facebook);

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
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAuth();
            }
        });
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookAuth();
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

        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    createUserData();
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
    private void facebookAuth() {
        LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(@NonNull FacebookException e) { }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    createUserData();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
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

    private void googleAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("631593816076-lv40qm1eibsgpul33djvhdcrjndh13dj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(RegisterActivity.this, gso);
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, GOOGLE_LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                String idToken = account.getIdToken();
                if(idToken != null) {

                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                createUserData();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (ApiException ex) {
                ex.printStackTrace();
                Toast.makeText(RegisterActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void createUserData() { //create object user in FirebaseDatabase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    String id_1 = "c_" + RandomString.random(10);
                    Map<String, Boolean> settings = new HashMap<>();
                    settings.put("notification", true);

                    database.getReference("users/" + user.getUid() + "/categories/" + id_1).setValue(new Category(id_1, "Công việc"));
                    String id_2 = "c_" + RandomString.random(10);
                    database.getReference("users/" + user.getUid() + "/categories/" + id_2).setValue(new Category(id_2, "Học tập"));
                    String id_3 = "c_" + RandomString.random(10);
                    database.getReference("users/" + user.getUid() + "/categories/" + id_3).setValue(new Category(id_3, "Giải trí"));
                    database.getReference("users/" + user.getUid() + "/settings").setValue(settings);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 1111;
    private CallbackManager callbackManager;
    LoadingDialog dialog = new LoadingDialog(LoginActivity.this);
    Toolbar toolbar;
    TextView toRegister, forgotPassword;
    EditText txtEmail, txtPassword;
    Button loginBtn;
    ImageView googleLogin, facebookLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_bx_arrow_back);

        toRegister = findViewById(R.id.toRegister);
        forgotPassword = findViewById(R.id.forgot_password);
        txtEmail = findViewById(R.id.login_email);
        txtPassword = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.btnLogin);
        googleLogin = findViewById(R.id.btn_login_google);
        facebookLogin = findViewById(R.id.btn_login_facebook);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth();
            }
        });
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickForgotPassword();
            }
        });
    }

    private void firebaseAuth() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(!validateData(email, password)) {
            return;
        }
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
    }

    private void googleAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("631593816076-lv40qm1eibsgpul33djvhdcrjndh13dj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(LoginActivity.this, gso);
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, GOOGLE_LOGIN_REQUEST_CODE);
    }

    private void facebookAuth() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onClickForgotPassword() {
        String email = txtEmail.getText().toString();
        if(email.length() == 0) {
            txtEmail.setError("Vui lòng nhập Email");
            txtEmail.requestFocus();
            return;
        }
        if(!EmailValidation.validateEmail(email)) {
            txtEmail.setError("Email không hợp lệ");
            txtEmail.requestFocus();
            return;
        }
        dialog.show();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Chúng tôi đã gửi email đến tài khoản email của bạn", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                String idToken = account.getIdToken();
                if(idToken != null) {
                    dialog.show();
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                createUserData();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });
                }
            } catch (ApiException ex) {
                ex.printStackTrace();
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
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

    private boolean validateData(String email, String password) {
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

        return true;
    }

}
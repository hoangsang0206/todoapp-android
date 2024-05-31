package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todoapp.databinding.ActivityUpdateUserProfileBinding;
import com.example.todoapp.dialogs.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

public class UpdateUserProfileActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1122;
    private static final int GALLERY_REQUEST_CODE = 1122;
    ActivityUpdateUserProfileBinding binding;
    Uri userImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_bx_arrow_back);

        showUserInformation();
        initUi();
    }

    private void initUi() {
        binding.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        binding.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = binding.userFullname.getText().toString();
                if(fullname.length() == 0) {
                    binding.userFullname.requestFocus();
                    binding.userFullname.setError("Vui lòng nhập tên");
                    return;
                }

                updateUserProfile(fullname);
            }
        });
    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        userImageUri = user.getPhotoUrl();
        Glide.with(UpdateUserProfileActivity.this)
                .load(user.getPhotoUrl()).error(R.drawable.user_no_image).into(binding.userImage);
        binding.userFullname.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
        binding.userEmail.setText(user.getEmail());
    }

    private void updateUserProfile(String fullname) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        LoadingDialog dialog = new LoadingDialog(UpdateUserProfileActivity.this);
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .setPhotoUri(userImageUri)
                .build();

        dialog.show();
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(UpdateUserProfileActivity.this,
                                    "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            MainActivity.getInstance().showUserInfomation();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateUserProfileActivity.this,
                                "Không thể thay đổi thông tin", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestPermission() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissions(
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), GALLERY_REQUEST_CODE);
    }

    private void showSelectedImage(Bitmap bitmap) {
        if(bitmap != null) {
            binding.userImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                userImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userImageUri);
                    showSelectedImage(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
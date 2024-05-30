package com.example.todoapp.popups;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todoapp.R;
import com.example.todoapp.databinding.PopupChangePasswordBinding;
import com.example.todoapp.databinding.PopupCreateCategoryBinding;
import com.example.todoapp.dialogs.LoadingDialog;
import com.example.todoapp.models.Category;
import com.example.todoapp.utils.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eightbitlab.com.blurview.RenderScriptBlur;

public class ChangePasswordPopup {
    public static void showPopup(Context context) {
        PopupChangePasswordBinding binding = PopupChangePasswordBinding.inflate(LayoutInflater.from(context));
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(),
                ConstraintLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        float radius = 1f;
        View decorView = ((Activity) context).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        binding.blurView.setupWith(rootView, new RenderScriptBlur(binding.getRoot().getContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);

        popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        popupWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);

        initCreateCategoryPopup(binding, popupWindow);
    }

    private static void initCreateCategoryPopup(PopupChangePasswordBinding binding, PopupWindow popup) {
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
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = binding.oldPassword.getText().toString();
                String newPass = binding.newPassword.getText().toString();
                String confirmPass = binding.confirmPassword.getText().toString();

                if(oldPass.length() == 0) {
                    binding.oldPassword.setError("Nhập mật khẩu cũ");
                    binding.oldPassword.requestFocus();
                    return;
                }
                if(newPass.length() == 0) {
                    binding.newPassword.setError("Nhập mật khẩu mới");
                    binding.newPassword.requestFocus();
                    return;
                }
                if(confirmPass.length() == 0 || !confirmPass.equals(newPass)) {
                    binding.confirmPassword.setError("Xác nhận mật khẩu không đúng");
                    binding.confirmPassword.requestFocus();
                    return;
                }

                changePassword(oldPass, newPass, binding, popup);
            }
        });
        binding.btnCancelChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.oldPassword.setText("");
                binding.newPassword.setText("");
                binding.confirmPassword.setText("");
                popup.dismiss();
            }
        });
    }

    private static void changePassword(String oldPassword, String newPassword, PopupChangePasswordBinding binding, PopupWindow popup) {
        Activity activity = (Activity) binding.getRoot().getContext();
        LoadingDialog dialog = new LoadingDialog(activity);
        dialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        popup.dismiss();
                                        Toast.makeText(binding.getRoot().getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        binding.oldPassword.setText("");
                                        binding.newPassword.setText("");
                                        binding.confirmPassword.setText("");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(binding.getRoot().getContext(), "Không thể đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(binding.getRoot().getContext(), "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

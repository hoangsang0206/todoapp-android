package com.example.todoapp.popups;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todoapp.R;
import com.example.todoapp.databinding.PopupEditCategoryBinding;
import com.example.todoapp.models.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eightbitlab.com.blurview.RenderScriptBlur;

public class EditCategoryPopup {
    public static void showPopup(Context context, Category category) {
        PopupEditCategoryBinding binding = PopupEditCategoryBinding.inflate(LayoutInflater.from(context));
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

        binding.categoryName.setText(category.getName());
        popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        popupWindow.showAtLocation(decorView, Gravity.CENTER, 0, 0);

        initCreateCategoryPopup(binding, popupWindow, category);
    }

    private static void initCreateCategoryPopup(PopupEditCategoryBinding binding, PopupWindow popup, Category category) {
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
        binding.btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.categoryName.getText().toString();
                if(name == null || name.isEmpty()) {
                    binding.categoryName.setError("Nhập tên danh mục");
                } else {
                    editCategory(category, name);
                    popup.dismiss();
                }
            }
        });
        binding.btnCancelEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    private static void editCategory(Category category, String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/categories/" + category.getId()).child("name");
        ref.setValue(name);
    }
}

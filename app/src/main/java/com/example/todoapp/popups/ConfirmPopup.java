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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todoapp.R;
import com.example.todoapp.databinding.PopupDeleteBinding;
import com.example.todoapp.models.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import eightbitlab.com.blurview.RenderScriptBlur;

public class ConfirmPopup {
    public static void showDeletePopup(Context context, Category category) {
        PopupDeleteBinding binding = PopupDeleteBinding.inflate(LayoutInflater.from(context));
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

        binding.popupMessage.setText("Danh mục này và các công việc liên quan sẽ bị xóa");
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categories/" + category.getId());
                Query query = FirebaseDatabase.getInstance().getReference("users/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/todoList").orderByChild("categoryId").equalTo(category.getId());

                ref.removeValue();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getChildren().forEach(s -> {
                                s.getRef().removeValue();
                            });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                popupWindow.dismiss();
            }
        });
    }
}

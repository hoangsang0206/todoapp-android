package com.example.todoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.databinding.CategoryListItemBinding;
import com.example.todoapp.databinding.PopupEditCategoryBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.popups.ConfirmPopup;
import com.example.todoapp.popups.EditCategoryPopup;
import com.example.todoapp.utils.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import eightbitlab.com.blurview.RenderScriptBlur;


public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CViewHolder> {
    private ArrayList<Category> categories;

    public CategoryRecyclerViewAdapter(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryListItemBinding binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.categoryName.setText(category.getName());

        Query query = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("todoList").orderByChild("categoryId").equalTo(category.getId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Todo> todoList = new ArrayList<>();
                snapshot.getChildren().forEach(s -> {
                    Todo todo = s.getValue(Todo.class);
                    todoList.add(todo);
                });

                holder.binding.todoCount.setText(String.valueOf(todoList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        holder.binding.editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCategoryPopup.showPopup(holder.binding.getRoot().getContext(), category);
            }
        });
        holder.binding.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmPopup.showDeletePopup(holder.binding.getRoot().getContext(), category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(ArrayList<Category> newCategories) {
        this.categories = newCategories;
    }


    public class CViewHolder extends RecyclerView.ViewHolder {
        private CategoryListItemBinding binding;

        public CViewHolder(@NonNull CategoryListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            binding = itemRowBinding;
        }
    }
}

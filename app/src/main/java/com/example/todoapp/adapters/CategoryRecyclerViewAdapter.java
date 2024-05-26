package com.example.todoapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.databinding.HorizonCategoryListItemBinding;
import com.example.todoapp.models.Category;

import java.util.ArrayList;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CViewHolder> {
    private ArrayList<Category> categories;
    private int clickedItem = RecyclerView.NO_POSITION;

    public CategoryRecyclerViewAdapter(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HorizonCategoryListItemBinding binding = HorizonCategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        holder.binding.categoryName.setText(categories.get(position).getName());

        if(clickedItem == position) {
            holder.binding.getRoot().setActivated(true);
        } else {
            holder.binding.getRoot().setActivated(false);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(clickedItem);
                clickedItem = holder.getAdapterPosition();
                notifyItemChanged(clickedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(ArrayList<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public class CViewHolder extends RecyclerView.ViewHolder {
        HorizonCategoryListItemBinding binding;

        public CViewHolder(@NonNull HorizonCategoryListItemBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }
    }
}

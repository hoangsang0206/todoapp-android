package com.example.todoapp.adapters;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.databinding.FragmentTodoBinding;
import com.example.todoapp.databinding.HorizonCategoryListItemBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.FilterTodoList;
import com.example.todoapp.utils.ParseDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class HorizonCategoryRecyclerViewAdapter extends RecyclerView.Adapter<HorizonCategoryRecyclerViewAdapter.CViewHolder> {
    private ArrayList<Category> categories;
    private int clickedItem = 0;
    private FragmentTodoBinding fragmentTodoBinding;

    public HorizonCategoryRecyclerViewAdapter(FragmentTodoBinding fragmentTodoBinding, ArrayList<Category> categories) {
        this.categories = categories;
        this.fragmentTodoBinding = fragmentTodoBinding;
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HorizonCategoryListItemBinding binding = HorizonCategoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.categoryName.setText(category.getName());

        if(clickedItem == position) {
            holder.binding.getRoot().setActivated(true);
            holder.binding.categoryName.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.binding.getRoot().setActivated(false);
            holder.binding.categoryName.setTextColor(Color.parseColor("#000000"));
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(clickedItem);
                clickedItem = holder.getAdapterPosition();
                notifyItemChanged(clickedItem);

                fragmentTodoBinding.todayTodo.setVisibility(View.GONE);
                fragmentTodoBinding.todayCompleted.setVisibility(View.GONE);
                fragmentTodoBinding.futureTodo.setVisibility(View.GONE);
                fragmentTodoBinding.previousTodo.setVisibility(View.GONE);
                fragmentTodoBinding.todoShimmer.setVisibility(View.VISIBLE);
                fragmentTodoBinding.todoEmpty.setVisibility(View.GONE);
                fragmentTodoBinding.todoShimmer.startShimmer();

                Query query;
                if(!category.getId().equals("all")) {
                    query = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("todoList").orderByChild("categoryId").equalTo(category.getId());
                } else {
                    query = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("todoList");
                }
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Todo> todoList = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Todo todo = dataSnapshot.getValue(Todo.class);
                            todoList.add(todo);
                        }

                        todoList.sort(Comparator.comparing(t -> {
                            LocalDateTime dateTime =  ParseDateTime.fromString(t.getDateToComplete());
                            return dateTime != null ? dateTime : LocalDateTime.MAX;
                        }));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTodoBinding.todoShimmer.stopShimmer();
                                fragmentTodoBinding.todoShimmer.setVisibility(View.INVISIBLE);

                                if(todoList.size() == 0) {
                                    fragmentTodoBinding.todayTodo.setVisibility(View.GONE);
                                    fragmentTodoBinding.todayCompleted.setVisibility(View.GONE);
                                    fragmentTodoBinding.futureTodo.setVisibility(View.GONE);
                                    fragmentTodoBinding.previousTodo.setVisibility(View.GONE);
                                    fragmentTodoBinding.todoEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    ArrayList<Todo> today = FilterTodoList.today(todoList);
                                    ArrayList<Todo> todayCompleted = FilterTodoList.todayCompleted(todoList);
                                    ArrayList<Todo> future = FilterTodoList.future(todoList);
                                    ArrayList<Todo> previous = FilterTodoList.previous(todoList);

                                    if(today.size() > 0) {
                                        fragmentTodoBinding.todayTodo.setVisibility(View.VISIBLE);
                                        fragmentTodoBinding.todayRcv.setAdapter(new TodoRecyclerViewAdapter(fragmentTodoBinding.getRoot().getContext(), today));
                                    } else {
                                        fragmentTodoBinding.todayTodo.setVisibility(View.GONE);
                                    }
                                    if(todayCompleted.size() > 0) {
                                        fragmentTodoBinding.todayCompleted.setVisibility(View.VISIBLE);
                                        fragmentTodoBinding.todayCompletedRcv.setAdapter(new TodoRecyclerViewAdapter(fragmentTodoBinding.getRoot().getContext(), todayCompleted));
                                    } else {
                                        fragmentTodoBinding.todayCompleted.setVisibility(View.GONE);
                                    }
                                    if(future.size() > 0) {
                                        fragmentTodoBinding.futureTodo.setVisibility(View.VISIBLE);
                                        fragmentTodoBinding.futureRcv.setAdapter(new TodoRecyclerViewAdapter(fragmentTodoBinding.getRoot().getContext(), future));
                                    } else {
                                        fragmentTodoBinding.futureTodo.setVisibility(View.GONE);
                                    }
                                    if(previous.size() > 0) {
                                        fragmentTodoBinding.previousTodo.setVisibility(View.VISIBLE);
                                        fragmentTodoBinding.previousRcv.setAdapter(new TodoRecyclerViewAdapter(fragmentTodoBinding.getRoot().getContext(), previous));
                                    } else {
                                        fragmentTodoBinding.previousTodo.setVisibility(View.GONE);
                                    }

                                    fragmentTodoBinding.todoEmpty.setVisibility(View.GONE);
                                }
                            }
                        }, 1000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
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

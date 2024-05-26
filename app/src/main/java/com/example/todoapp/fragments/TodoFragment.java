package com.example.todoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todoapp.adapters.CategoryRecyclerViewAdapter;
import com.example.todoapp.adapters.TodoRecyclerViewAdapter;
import com.example.todoapp.databinding.FragmentTodoBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.RandomString;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TodoFragment extends Fragment {
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private ArrayList<Category> categories;
    private ArrayList<Todo> todoList;
    private FragmentTodoBinding binding;

    public TodoFragment() {
        // Required empty public constructor
    }

    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoBinding.inflate(inflater, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        LinearLayoutManager horizonLinearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.todoRecyclerView.setLayoutManager(linearLayoutManager);
        binding.horizonCategoryRcview.setLayoutManager(horizonLinearLayoutManager);

        getCategories();
        getTodoList();

        return binding.getRoot();
    }

    private void createCategory(String categoryName) {
        String cateId = "c_" + RandomString.random(10);
        Category category = new Category(cateId, categoryName);

        DatabaseReference ref = db.getReference("users/" + user.getUid() + "/categories/" + cateId);
        ref.setValue(category, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                //...........
            }
        });
    }

    private void getCategories() {
        binding.horizonCategoryRcview.setVisibility(View.INVISIBLE);
        binding.categoriesShimmer.setVisibility(View.VISIBLE);
        binding.categoriesShimmer.startShimmer();
        DatabaseReference ref = db.getReference("users/" + user.getUid() + "/categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    categories = new ArrayList<>();
                    categories.add(new Category("all", "Tất cả"));
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Category category = dataSnapshot.getValue(Category.class);
                        categories.add(category);
                    }

                    binding.horizonCategoryRcview.setAdapter(new CategoryRecyclerViewAdapter(binding, categories));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.horizonCategoryRcview.setVisibility(View.VISIBLE);
                            binding.categoriesShimmer.stopShimmer();
                            binding.categoriesShimmer.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getTodoList() {
        binding.todoRecyclerView.setVisibility(View.INVISIBLE);
        binding.todoShimmer.setVisibility(View.VISIBLE);
        binding.todoShimmer.startShimmer();

        DatabaseReference ref = db.getReference("users/" + user.getUid() + "/todoList");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    todoList = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Todo todo = dataSnapshot.getValue(Todo.class);
                        todoList.add(todo);
                    }

                    binding.todoRecyclerView.setAdapter(new TodoRecyclerViewAdapter(getContext(), todoList));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.todoShimmer.stopShimmer();
                            binding.todoShimmer.setVisibility(View.INVISIBLE);
                            binding.todoRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
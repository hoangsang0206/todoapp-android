package com.example.todoapp.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.adapters.CategoryRecyclerViewAdapter;
import com.example.todoapp.adapters.HorizonCategoryRecyclerViewAdapter;
import com.example.todoapp.databinding.FragmentCategoriesBinding;
import com.example.todoapp.databinding.PopupCreateCategoryBinding;
import com.example.todoapp.databinding.PopupEditCategoryBinding;
import com.example.todoapp.models.Category;
import com.example.todoapp.popups.CreateCategoryPopup;
import com.example.todoapp.popups.CreateTodoPopup;
import com.example.todoapp.utils.RandomString;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    FragmentCategoriesBinding binding;
    ArrayList<Category> categories;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        binding.categoryRcview.setLayoutManager(linearLayoutManager);
        getCategories();

        binding.btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCategoryPopup.showPopup(getContext());
            }
        });

        return binding.getRoot();
    }

    private void getCategories() {
        binding.categoryRcview.setVisibility(View.INVISIBLE);
        binding.categoriesShimmer.startShimmer();
        binding.categoriesShimmer.setVisibility(View.VISIBLE);
        binding.categoriesEmpty.setVisibility(View.GONE);

        DatabaseReference ref = db.getReference("users/" + user.getUid() + "/categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categories.add(category);
                }

                binding.categoryRcview.setAdapter(new CategoryRecyclerViewAdapter(categories));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(categories.size() == 0) {
                            binding.categoriesEmpty.setVisibility(View.VISIBLE);
                            binding.categoryRcview.setVisibility(View.VISIBLE);
                            binding.categoriesShimmer.stopShimmer();
                            binding.categoriesShimmer.setVisibility(View.INVISIBLE);
                        } else {
                            binding.categoryRcview.setVisibility(View.VISIBLE);
                            binding.categoriesShimmer.stopShimmer();
                            binding.categoriesShimmer.setVisibility(View.INVISIBLE);
                            binding.categoriesEmpty.setVisibility(View.GONE);
                        }
                    }
                }, 1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
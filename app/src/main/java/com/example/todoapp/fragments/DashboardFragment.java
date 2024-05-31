package com.example.todoapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.todoapp.R;
import com.example.todoapp.adapters.TodoRecyclerViewAdapter;
import com.example.todoapp.databinding.FragmentDashboardBinding;
import com.example.todoapp.models.Todo;
import com.example.todoapp.utils.FilterTodoList;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL ,false);
        binding.weekRcview.setLayoutManager(linearLayoutManager);

        showUserInfomation();
        getData();
        return binding.getRoot();
    }

    private void getData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        ArrayList<Todo> todoList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("todoList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int completed = 0;
                        for(DataSnapshot snap : snapshot.getChildren()) {
                            Todo todo = snap.getValue(Todo.class);
                            todoList.add(todo);
                            if(todo.isCompleteStatus()) {
                                completed++;
                            }
                        }

                        ArrayList<Todo> weekTodoList = FilterTodoList.week(todoList);
                        loadWeekTodoList(todoList);
                        showOverviewTaskCount(completed, todoList.size());
                        showChart(weekTodoList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }

        Glide.with(binding.userImg).load(user.getPhotoUrl()).error(R.drawable.user_no_image).into(binding.userImg);
        binding.userFullname.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
        binding.userEmail.setText(user.getEmail());
    }

    public void loadWeekTodoList(ArrayList<Todo> weekTodoList) {
        binding.weekRcview.setAdapter(new TodoRecyclerViewAdapter(getContext(), weekTodoList));
    }

    public void showOverviewTaskCount(int completed, int total) {
        binding.completedCount.setText(String.valueOf(completed));
        binding.incompleteCount.setText(String.valueOf(total));
    }

    private void showChart(ArrayList<Todo> weekTodoList) {
        ArrayList<Todo> monday = FilterTodoList.day(weekTodoList, DayOfWeek.MONDAY);
        ArrayList<Todo> tuesday = FilterTodoList.day(weekTodoList, DayOfWeek.TUESDAY);
        ArrayList<Todo> wednesday = FilterTodoList.day(weekTodoList, DayOfWeek.WEDNESDAY);
        ArrayList<Todo> thursday = FilterTodoList.day(weekTodoList, DayOfWeek.THURSDAY);
        ArrayList<Todo> friday = FilterTodoList.day(weekTodoList, DayOfWeek.FRIDAY);
        ArrayList<Todo> saturday = FilterTodoList.day(weekTodoList, DayOfWeek.SATURDAY);
        ArrayList<Todo> sunday = FilterTodoList.day(weekTodoList, DayOfWeek.SUNDAY);

        ArrayList<ArrayList<Todo>> data = new ArrayList<>();
        data.add(monday);
        data.add(tuesday);
        data.add(wednesday);
        data.add(thursday);
        data.add(friday);
        data.add(saturday);
        data.add(sunday);

        String[] daysOfWeek = {"Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy", "CN"};
        CombinedData combinedData = new CombinedData();
        combinedData.setData(generateLineData(data));
        combinedData.setData(generateBarData(data));

        XAxis xAxis = binding.combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        xAxis.setTextSize(12f);

        binding.combinedChart.getAxisLeft().setTextSize(12f);
        binding.combinedChart.getAxisRight().setTextSize(12f);
        binding.combinedChart.getLegend().setTextSize(12f);

        binding.combinedChart.setData(combinedData);
        binding.combinedChart.invalidate();
    }

    private LineData generateLineData(ArrayList<ArrayList<Todo>> data) {
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            ArrayList<Todo> item = data.get(i);
            entries.add(new Entry(
                    i,
                    FilterTodoList.completed(item).size()
            ));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Đã hoàn thành");
        lineDataSet.setColor(Color.parseColor("#c5ff8c"));
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleColor(Color.parseColor("#c5ff8c"));
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        return new LineData(dataSets);
    }

    private BarData generateBarData(ArrayList<ArrayList<Todo>> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            ArrayList<Todo> item = data.get(i);
            entries.add(new BarEntry(
                    i,
                    item.size()
            ));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số công việc");
        dataSet.setColor(Color.parseColor("#1877f2"));
        dataSet.setValueTextSize(12f);

        return new BarData(dataSet);
    }
}
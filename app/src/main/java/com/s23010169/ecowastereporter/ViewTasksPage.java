package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.TaskAdapter;
import com.s23010169.ecowastereporter.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewTasksPage extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private TabLayout tabLayout;
    private TextView taskCountTextView;
    private List<Task> allTasks;
    private List<Task> currentDisplayedTasks;
    private FloatingActionButton refreshFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks_page);

        // Initialize views
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        taskCountTextView = findViewById(R.id.taskCountTextView);
        refreshFab = findViewById(R.id.refreshFab);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize task lists
        allTasks = new ArrayList<>();
        currentDisplayedTasks = new ArrayList<>();

        // Initialize RecyclerView
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(currentDisplayedTasks, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Load initial data
        loadTasks();

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTasks(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Handle refresh button
        refreshFab.setOnClickListener(v -> refreshTasks());
    }

    private void loadTasks() {
        try {
            allTasks = createSampleTasks();
            filterTasks(tabLayout.getSelectedTabPosition());
        } catch (Exception e) {
            handleError("Error loading tasks", e);
        }
    }

    private List<Task> createSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Main Street Corner", "HIGH", 95, "2 citizen reports", 0.3, 15));
        tasks.add(new Task("City Park Entrance", "MED", 78, "Scheduled cleaning", 0.8, 10));
        tasks.add(new Task("Shopping Mall", "LOW", 45, "Routine check", 1.2, 8));
        return tasks;
    }

    private void filterTasks(int tabPosition) {
        try {
            if (allTasks == null) {
                handleError("Task list is null", new NullPointerException());
                return;
            }

            currentDisplayedTasks.clear();
            
            switch (tabPosition) {
                case 0: // Urgent
                    for (Task task : allTasks) {
                        if ("HIGH".equals(task.getPriority())) {
                            currentDisplayedTasks.add(task);
                        }
                    }
                    break;
                case 1: // All Tasks
                    currentDisplayedTasks.addAll(allTasks);
                    break;
                case 2: // Route
                    currentDisplayedTasks.addAll(allTasks);
                    Collections.sort(currentDisplayedTasks, 
                        (t1, t2) -> Double.compare(t1.getEstimatedDistance(), t2.getEstimatedDistance()));
                    break;
            }
            
            taskAdapter.updateTasks(new ArrayList<>(currentDisplayedTasks));
            updateTaskCount();
        } catch (Exception e) {
            handleError("Error filtering tasks", e);
        }
    }

    private void updateTaskCount() {
        try {
            int totalTasks = allTasks != null ? allTasks.size() : 0;
            taskCountTextView.setText(totalTasks + " tasks remaining");
        } catch (Exception e) {
            handleError("Error updating task count", e);
        }
    }

    private void refreshTasks() {
        try {
            // Animate the FAB
            refreshFab.animate()
                    .rotation(refreshFab.getRotation() + 360)
                    .setDuration(1000)
                    .start();
            
            loadTasks();
            Toast.makeText(this, "Tasks refreshed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            handleError("Error refreshing tasks", e);
        }
    }

    @Override
    public void onGetRouteClick(Task task) {
        try {
            if (task != null) {
                Toast.makeText(this, "Getting route to " + task.getLocation(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            handleError("Error getting route", e);
        }
    }

    @Override
    public void onMarkDoneClick(Task task) {
        try {
            if (task == null) {
                handleError("Cannot mark null task as done", new IllegalArgumentException());
                return;
            }

            // Remove from both lists to maintain consistency
            allTasks.remove(task);
            currentDisplayedTasks.remove(task);
            
            // Update the adapter with the current displayed tasks
            taskAdapter.updateTasks(new ArrayList<>(currentDisplayedTasks));
            
            // Update the task count
            updateTaskCount();
            
            Toast.makeText(this, "Task completed: " + task.getLocation(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            handleError("Error marking task as done", e);
        }
    }

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        Toast.makeText(this, message + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
} 
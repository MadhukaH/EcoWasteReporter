package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.s23010169.ecowastereporter.UpdateBinStatusPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.s23010169.ecowastereporter.adapters.ActionAdapter;
import com.s23010169.ecowastereporter.models.Action;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;

public class CleanerHomePage extends AppCompatActivity implements ActionAdapter.ActionClickListener {
    private TextView cleanerName;
    private MaterialCardView tasksCard, completedCard;
    private RecyclerView actionsRecyclerView;
    private LinearProgressIndicator taskProgress;
    private ExtendedFloatingActionButton startRouteButton;
    private ImageView notificationIcon, profileIcon;
    private CleanerDatabaseHelper databaseHelper;
    private String userEmail;
    private TextView tasksCountTextView, completedCountTextView, taskProgressText;

    @Override
    public void onBackPressed() {
        // Prevent going back to login/registration screens
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_home_page);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        
        // Initialize database helper
        databaseHelper = new CleanerDatabaseHelper(this);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        updateTaskStatsAndProgress();
        displayCleanerName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskStatsAndProgress();
    }

    private void initializeViews() {
        cleanerName = findViewById(R.id.cleanerName);
        tasksCard = findViewById(R.id.tasksCard);
        completedCard = findViewById(R.id.completedCard);
        actionsRecyclerView = findViewById(R.id.actionsRecyclerView);
        taskProgress = findViewById(R.id.taskProgress);
        startRouteButton = findViewById(R.id.startRouteButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        tasksCountTextView = findViewById(R.id.tasksCount);
        completedCountTextView = findViewById(R.id.completedCount);
        taskProgressText = findViewById(R.id.taskProgressText);
    }

    private void displayCleanerName() {
        if (userEmail != null) {
            Cleaner cleaner = databaseHelper.getCleanerByEmail(userEmail);
            if (cleaner != null) {
                cleanerName.setText(cleaner.getName() + "! 👋");
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView() {
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(R.drawable.ic_route_map, "Route Map", () -> handleActionClick("Route Map")));
        actions.add(new Action(R.drawable.ic_mark_complete, "Mark Complete", () -> handleActionClick("Mark Complete")));
        actions.add(new Action(R.drawable.ic_view_tasks, "View Tasks", () -> handleActionClick("View Tasks")));
        actions.add(new Action(R.drawable.ic_performance, "Performance", () -> handleActionClick("Performance")));

        ActionAdapter adapter = new ActionAdapter(this, actions, this);
        actionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        actionsRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        tasksCard.setOnClickListener(v -> showTasksDetail());
        completedCard.setOnClickListener(v -> showCompletedTasksDetail());
        startRouteButton.setOnClickListener(v -> startRoute());
        notificationIcon.setOnClickListener(v -> showNotifications());
        profileIcon.setOnClickListener(v -> navigateToProfile());
    }

    private void updateTaskStatsAndProgress() {
        // Fetch all tasks for today for this cleaner
        // For now, we use all reports as tasks (simulate today's tasks)
        com.s23010169.ecowastereporter.models.ReportDatabaseHelper reportDb = new com.s23010169.ecowastereporter.models.ReportDatabaseHelper(this);
        java.util.List<com.s23010169.ecowastereporter.models.Report> allReports = reportDb.getAllReports();
        int totalTasks = 0;
        int completedTasks = 0;
        long todayStart = getTodayStartMillis();
        for (com.s23010169.ecowastereporter.models.Report report : allReports) {
            // Only count reports assigned to this cleaner (if such logic exists)
            // For now, count all reports from today
            if (report.getTimestamp() >= todayStart) {
                totalTasks++;
                if ("Resolved".equalsIgnoreCase(report.getStatus())) {
                    completedTasks++;
                }
            }
        }
        // Avoid division by zero
        int percent = (totalTasks > 0) ? (int) ((completedTasks * 100.0f) / totalTasks) : 0;
        // Update UI
        taskProgress.setProgress(percent);
        if (taskProgressText != null) {
            taskProgressText.setText(percent + "% of today's tasks completed");
        }
        if (tasksCountTextView != null) {
            tasksCountTextView.setText(String.valueOf(totalTasks));
        }
        if (completedCountTextView != null) {
            completedCountTextView.setText(String.valueOf(completedTasks));
        }
    }

    private long getTodayStartMillis() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public void onActionClick(Action action) {
        handleActionClick(action.getTitle());
    }

    private void handleActionClick(String actionTitle) {
        switch (actionTitle) {
            case "Route Map":
                showRouteMap();
                break;
            case "Mark Complete":
                showMarkComplete();
                break;
            case "View Tasks":
                showTasksList();
                break;
            case "Performance":
                showPerformanceStats();
                break;
        }
    }

    private void showTasksDetail() {
        showToast("Showing detailed tasks breakdown...");
        // TODO: Implement tasks detail view
    }

    private void showCompletedTasksDetail() {
        showToast("Showing completed tasks history...");
        // TODO: Implement completed tasks history
    }

    private void startRoute() {
        showToast("Starting route navigation...");
        startRouteButton.extend(); // Show full FAB text
        // TODO: Implement route navigation
    }

    private void showNotifications() {
        showToast("Opening notifications...");
        // TODO: Implement notifications view
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, CleanerProfilePage.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void showRouteMap() {
        Intent intent = new Intent(this, RouteMapPage.class);
        startActivity(intent);
    }

    private void showMarkComplete() {
        Intent intent = new Intent(this, UpdateBinStatusPage.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void showTasksList() {
        showToast("Opening tasks list...");
        Intent intent = new Intent(this, ViewTasksPage.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    private void showPerformanceStats() {
        Intent intent = new Intent(this, CleanerPerformancePage.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 
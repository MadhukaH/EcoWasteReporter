package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.s23010169.ecowastereporter.models.Task;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// This screen shows performance metrics for waste collection workers including tasks completed and efficiency.
public class CleanerPerformancePage extends AppCompatActivity {
    private TextView tvCurrentDate;
    private TextView tvTasksCompleted;
    private TextView tvEfficiencyRate;
    private RecyclerView rvRecentActivities;
    private ExtendedFloatingActionButton fabStartTask;
    private View emptyStateLayout;
    private MaterialButton btnStartNewTask;
    private CleanerDatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_performance_page);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();

        // Initialize database helper
        dbHelper = new CleanerDatabaseHelper(this);

        // Set current date
        setCurrentDate();

        // Load performance metrics
        updatePerformanceMetrics();

        // Load recent activities
        loadRecentActivities();

        // Setup click listeners
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePerformanceMetrics();
    }

    private void initializeViews() {
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvTasksCompleted = findViewById(R.id.tvTasksCompleted);
        tvEfficiencyRate = findViewById(R.id.tvEfficiencyRate);
        rvRecentActivities = findViewById(R.id.rvRecentActivities);
        fabStartTask = findViewById(R.id.fabStartTask);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnStartNewTask = findViewById(R.id.btnStartNewTask);
        
        // Set up RecyclerView
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivities.setNestedScrollingEnabled(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        tvCurrentDate.setText(currentDate);
    }

    private void updatePerformanceMetrics() {
        // Use the same logic as CleanerHomePage for today's stats
        com.s23010169.ecowastereporter.models.ReportDatabaseHelper reportDb = new com.s23010169.ecowastereporter.models.ReportDatabaseHelper(this);
        java.util.List<com.s23010169.ecowastereporter.models.Report> allReports = reportDb.getAllReports();
        int totalTasks = 0;
        int completedTasks = 0;
        long todayStart = getTodayStartMillis();
        for (com.s23010169.ecowastereporter.models.Report report : allReports) {
            if (report.getTimestamp() >= todayStart) {
                totalTasks++;
                if ("Resolved".equalsIgnoreCase(report.getStatus())) {
                    completedTasks++;
                }
            }
        }
        // Efficiency: percent of completed tasks
        int percent = (totalTasks > 0) ? (int) ((completedTasks * 100.0f) / totalTasks) : 0;
        animateNumber(tvTasksCompleted, 0, completedTasks, 1000);
        animateNumber(tvEfficiencyRate, 0, percent, 1500);
    }

    private long getTodayStartMillis() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void loadRecentActivities() {
        // TODO: Load actual recent activities from database
        // For now, showing empty state
        rvRecentActivities.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        fabStartTask.setOnClickListener(v -> startNewTask());
        btnStartNewTask.setOnClickListener(v -> startNewTask());

        // Show/hide FAB on scroll
        rvRecentActivities.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabStartTask.shrink();
                } else {
                    fabStartTask.extend();
                }
            }
        });
    }

    private void startNewTask() {
        Intent intent = new Intent(this, ViewTasksPage.class);
        startActivity(intent);
    }

    private void animateNumber(final TextView textView, int start, int end, long duration) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            if (textView == tvEfficiencyRate) {
                textView.setText(value + "%");
            } else {
                textView.setText(String.valueOf(value));
            }
        });
        animator.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 
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

public class CleanerPerformancePage extends AppCompatActivity {
    private TextView tvCurrentDate;
    private TextView tvTasksCompleted;
    private TextView tvEfficiencyRate;
    private RecyclerView rvRecentActivities;
    private ExtendedFloatingActionButton fabStartTask;
    private View emptyStateLayout;
    private MaterialButton btnStartNewTask;
    private CleanerDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_performance_page);

        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();

        // Initialize database helper
        dbHelper = new CleanerDatabaseHelper(this);

        // Set current date
        setCurrentDate();

        // Load performance metrics
        loadPerformanceMetrics();

        // Load recent activities
        loadRecentActivities();

        // Setup click listeners
        setupClickListeners();
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

    private void loadPerformanceMetrics() {
        // TODO: Load actual metrics from database
        // For now, setting sample data with animation
        animateNumber(tvTasksCompleted, 0, 25, 1000);
        animateNumber(tvEfficiencyRate, 0, 85, 1500);
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
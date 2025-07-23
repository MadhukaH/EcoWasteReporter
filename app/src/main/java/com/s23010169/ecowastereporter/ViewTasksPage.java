package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.TaskAdapter;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import com.s23010169.ecowastereporter.models.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ViewTasksPage extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private TabLayout tabLayout;
    private TextView taskCountTextView;
    private List<Task> allTasks;
    private List<Task> currentDisplayedTasks;
    private FloatingActionButton refreshFab;
    private ReportDatabaseHelper reportDatabaseHelper;
    private Random random = new Random();
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks_page);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");

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
        
        // Set toolbar title with cleaner name if available
        if (userEmail != null) {
            try {
                CleanerDatabaseHelper cleanerDb = new CleanerDatabaseHelper(this);
                Cleaner cleaner = cleanerDb.getCleanerByEmail(userEmail);
                if (cleaner != null) {
                    getSupportActionBar().setTitle("Tasks - " + cleaner.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize task lists
        allTasks = new ArrayList<>();
        currentDisplayedTasks = new ArrayList<>();

        // Initialize RecyclerView
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(currentDisplayedTasks, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Initialize ReportDatabaseHelper
        reportDatabaseHelper = new ReportDatabaseHelper(this);

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
            allTasks = fetchTasksFromReports();
            filterTasks(tabLayout.getSelectedTabPosition());
        } catch (Exception e) {
            handleError("Error loading tasks", e);
        }
    }

    private List<Task> fetchTasksFromReports() {
        List<Task> tasks = new ArrayList<>();
        List<Report> reports = reportDatabaseHelper.getAllReports();
        
        // Only create tasks for reports that are not 'Resolved'
        for (Report report : reports) {
            if (!"Resolved".equalsIgnoreCase(report.getStatus())) {
                Task task = createTaskFromReport(report);
                tasks.add(task);
            }
        }
        
        // Do not show any sample/demo tasks if there are none
        return tasks;
    }

    private List<Task> createSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();
        
        // Sample task data
        String[] locations = {"Main Street Corner", "Central Park", "Market Square", "Beach Front", "Downtown Plaza", "University Campus", "Shopping Mall", "Residential Area"};
        String[] wasteTypes = {"General Waste", "Recyclables", "Organic Waste", "Hazardous Waste", "Electronic Waste"};
        String[] descriptions = {"Bin overflow reported", "Regular maintenance needed", "Cleaning required", "Waste collection overdue", "Bin damage reported"};
        
        for (int i = 0; i < 8; i++) {
            String taskId = String.format("#T2024-%03d", 150 + i);
            String location = locations[i];
            String wasteType = wasteTypes[random.nextInt(wasteTypes.length)];
            String description = descriptions[random.nextInt(descriptions.length)];
            String priority = getRandomPriority();
            int binFullPercentage = 60 + random.nextInt(40); // 60-100%
            double estimatedDistance = 0.5 + random.nextDouble() * 2.0; // 0.5-2.5 km
            int estimatedTime = 10 + random.nextInt(20); // 10-30 minutes
            
            Task task = new Task(location, priority, binFullPercentage, wasteType, estimatedDistance, estimatedTime);
            task.setTaskId(taskId);
            task.setDescription(description);
            task.setStatus("Pending");
            task.setReportId("SAMPLE-" + (i + 1));
            sampleTasks.add(task);
        }
        
        return sampleTasks;
    }

    private Task createTaskFromReport(Report report) {
        String location = report.getLocation();
        String description = report.getDescription();
        String status = report.getStatus();
        String priority = determinePriority(report);
        int binFullPercentage = calculateBinFullPercentage(report);
        String additionalInfo = report.getWasteType();
        double estimatedDistance = calculateEstimatedDistance(report);
        int estimatedTime = calculateEstimatedTime(report);
        
        Task task = new Task(location, priority, binFullPercentage, additionalInfo, estimatedDistance, estimatedTime);
        task.setDescription(description);
        task.setStatus(status);
        task.setReportId(report.getReportId());
        task.setTaskId(generateTaskId(report));
        
        return task;
    }

    private String getRandomPriority() {
        String[] priorities = {"HIGH", "MED", "LOW"};
        return priorities[random.nextInt(priorities.length)];
    }

    private String determinePriority(Report report) {
        // Robust priority logic based on waste type (ignore spaces and case)
        String wasteTypeRaw = report.getWasteType() != null ? report.getWasteType() : "";
        String wasteType = wasteTypeRaw.trim().toLowerCase().replaceAll("\\s+", "");

        if (wasteType.equals("brokenbin")) {
            return "HIGH";
        } else if (wasteType.equals("overflowingbin")) {
            return "MED";
        } else if (wasteType.equals("illegaldumping")) {
            return "LOW";
        }
        // Fallback to previous logic for other types
        String description = report.getDescription() != null ? report.getDescription().toLowerCase() : "";
        if (wasteType.contains("hazardous") || wasteType.contains("electronic") || 
            description.contains("overflow") || description.contains("urgent")) {
            return "HIGH";
        } else if (wasteType.contains("organic") || description.contains("maintenance")) {
            return "MED";
        } else {
            return "LOW";
        }
    }

    private int calculateBinFullPercentage(Report report) {
        // Calculate based on waste type and description
        String description = report.getDescription().toLowerCase();
        if (description.contains("overflow") || description.contains("full")) {
            return 90 + random.nextInt(10); // 90-100%
        } else if (description.contains("half") || description.contains("medium")) {
            return 50 + random.nextInt(20); // 50-70%
        } else {
            return 30 + random.nextInt(30); // 30-60%
        }
    }

    private double calculateEstimatedDistance(Report report) {
        // Simulate distance calculation based on location
        return 0.5 + random.nextDouble() * 3.0; // 0.5-3.5 km
    }

    private int calculateEstimatedTime(Report report) {
        // Calculate estimated time based on waste type and priority
        String wasteType = report.getWasteType().toLowerCase();
        if (wasteType.contains("hazardous") || wasteType.contains("electronic")) {
            return 20 + random.nextInt(15); // 20-35 minutes
        } else if (wasteType.contains("organic")) {
            return 15 + random.nextInt(10); // 15-25 minutes
        } else {
            return 10 + random.nextInt(10); // 10-20 minutes
        }
    }

    private String generateTaskId(Report report) {
        // Generate task ID based on report ID or timestamp
        if (report.getReportId() != null) {
            return "#T" + report.getReportId();
        } else {
            return "#T" + System.currentTimeMillis() % 10000;
        }
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
                // TODO: Implement actual route navigation
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

            // Only navigate to UpdateBinStatusPage, do not mark as done here
            Intent intent = new Intent(this, UpdateBinStatusPage.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        } catch (Exception e) {
            handleError("Error navigating to update bin status page", e);
        }
    }

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        Toast.makeText(this, message + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
} 
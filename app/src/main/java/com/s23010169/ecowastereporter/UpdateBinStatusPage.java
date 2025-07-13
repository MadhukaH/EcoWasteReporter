package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.s23010169.ecowastereporter.adapters.TaskSelectorAdapter;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import com.s23010169.ecowastereporter.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UpdateBinStatusPage extends AppCompatActivity implements TaskSelectorAdapter.TaskSelectionListener {
    private RadioButton radioFullyCleaned;
    private EditText editTextNotes;
    private Button btnSaveDraft, btnCompleteTask;
    private CardView photoUploadCard;
    private RecyclerView taskSelectorRecyclerView;
    private TaskSelectorAdapter taskAdapter;
    private TextView locationText, taskDetailsText;
    private ReportDatabaseHelper reportDatabaseHelper;
    private List<Task> availableTasks;
    private Task selectedTask;
    private Random random = new Random();
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bin_status_page);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");

        // Initialize database helper
        reportDatabaseHelper = new ReportDatabaseHelper(this);

        // Initialize views
        initializeViews();
        setupToolbar();
        loadTasksFromDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        radioFullyCleaned = findViewById(R.id.radioFullyCleaned);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        photoUploadCard = findViewById(R.id.photoUploadCard);
        taskSelectorRecyclerView = findViewById(R.id.taskSelectorRecyclerView);
        locationText = findViewById(R.id.locationText);
        taskDetailsText = findViewById(R.id.taskDetailsText);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Set toolbar title with cleaner name if available
        if (userEmail != null) {
            try {
                CleanerDatabaseHelper cleanerDb = new CleanerDatabaseHelper(this);
                Cleaner cleaner = cleanerDb.getCleanerByEmail(userEmail);
                if (cleaner != null) {
                    getSupportActionBar().setTitle("Cleaner: " + cleaner.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTasksFromDatabase() {
        try {
            availableTasks = new ArrayList<>();
            List<Report> reports = reportDatabaseHelper.getAllReports();
            
            // If no reports exist, create sample tasks for demonstration
            if (reports.isEmpty()) {
                availableTasks = createSampleTasks();
            } else {
                // Convert reports to tasks
                for (Report report : reports) {
                    if (!"Resolved".equals(report.getStatus())) {
                        Task task = createTaskFromReport(report);
                        availableTasks.add(task);
                    }
                }
            }
            
            // If still no tasks, create some sample ones
            if (availableTasks.isEmpty()) {
                availableTasks = createSampleTasks();
            }
            
            setupTaskSelector();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback to sample tasks
            availableTasks = createSampleTasks();
            setupTaskSelector();
        }
    }

    private List<Task> createSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();
        
        String[] locations = {"Main Street Corner", "Central Park", "Market Square", "Beach Front", "Downtown Plaza"};
        String[] descriptions = {"Bin cleaning and maintenance required", "Regular waste collection needed", "Bin overflow reported", "Weekly maintenance check", "Emergency cleanup needed"};
        String[] statuses = {"In Progress", "Pending", "In Progress", "Pending", "In Progress"};
        String[] distances = {"0.3 km away", "0.8 km away", "1.2 km away", "1.5 km away", "0.6 km away"};
        
        for (int i = 0; i < 5; i++) {
            String taskId = String.format("#T2024-%03d", 150 + i);
            Task task = new Task(taskId, locations[i], descriptions[i], statuses[i], distances[i]);
            task.setReportId("SAMPLE-" + (i + 1));
            sampleTasks.add(task);
        }
        
        return sampleTasks;
    }

    private Task createTaskFromReport(Report report) {
        String taskId = generateTaskId(report);
        String location = report.getLocation();
        String description = report.getDescription();
        String status = report.getStatus();
        String distance = calculateDistance(report);
        
        Task task = new Task(taskId, location, description, status, distance);
        task.setReportId(report.getReportId());
        
        return task;
    }

    private String generateTaskId(Report report) {
        if (report.getReportId() != null) {
            return "#T" + report.getReportId();
        } else {
            return "#T" + System.currentTimeMillis() % 10000;
        }
    }

    private String calculateDistance(Report report) {
        // Simulate distance calculation
        double distance = 0.3 + random.nextDouble() * 2.0; // 0.3-2.3 km
        return String.format("%.1f km away", distance);
    }

    private void setupTaskSelector() {
        // Setup RecyclerView
        taskAdapter = new TaskSelectorAdapter(this, availableTasks, this);
        taskSelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskSelectorRecyclerView.setAdapter(taskAdapter);

        // Set initial task if available
        if (!availableTasks.isEmpty()) {
            onTaskSelected(availableTasks.get(0), 0);
        }
    }

    private void setupClickListeners() {
        btnSaveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDraft();
            }
        });

        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask();
            }
        });

        photoUploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateBinStatusPage.this, "Photo upload coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDraft() {
        if (selectedTask == null) {
            Toast.makeText(this, "Please select a task first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = editTextNotes.getText().toString().trim();
        boolean isFullyCleaned = radioFullyCleaned.isChecked();
        
        // Save draft logic here
        Toast.makeText(this, "Draft saved for task: " + selectedTask.getTaskId(), Toast.LENGTH_SHORT).show();
    }

    private void completeTask() {
        if (selectedTask == null) {
            Toast.makeText(this, "Please select a task first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = editTextNotes.getText().toString().trim();
        boolean isFullyCleaned = radioFullyCleaned.isChecked();
        
        try {
            // Update the report status in database if it's a real report
            if (selectedTask.getReportId() != null && !selectedTask.getReportId().startsWith("SAMPLE-")) {
                int updated = reportDatabaseHelper.updateReportStatus(selectedTask.getReportId(), "Resolved");
                if (updated > 0) {
                    Toast.makeText(this, "Task completed and report resolved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show();
            }
            
            // Remove the completed task from the list
            availableTasks.remove(selectedTask);
            taskAdapter.notifyDataSetChanged();
            
            // Select next task if available
            if (!availableTasks.isEmpty()) {
                onTaskSelected(availableTasks.get(0), 0);
            } else {
                // No more tasks
                locationText.setText("No tasks available");
                taskDetailsText.setText("All tasks completed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error completing task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskSelected(Task task, int position) {
        selectedTask = task;
        
        // Update location card with selected task details
        locationText.setText(task.getLocation());
        taskDetailsText.setText(String.format("Task ID: %s • %s", task.getTaskId(), task.getDescription()));
        
        // Update UI based on task status
        updateUIForTaskStatus(task.getStatus());
    }

    private void updateUIForTaskStatus(String status) {
        // Update UI elements based on task status
        if ("In Progress".equals(status)) {
            btnCompleteTask.setEnabled(true);
            btnCompleteTask.setText("Complete Task");
        } else if ("Pending".equals(status)) {
            btnCompleteTask.setEnabled(true);
            btnCompleteTask.setText("Start Task");
        } else {
            btnCompleteTask.setEnabled(false);
            btnCompleteTask.setText("Task Completed");
        }
    }
} 
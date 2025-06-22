package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.s23010169.ecowastereporter.adapters.TaskSelectorAdapter;
import com.s23010169.ecowastereporter.models.Task;

import java.util.ArrayList;
import java.util.List;

public class UpdateBinStatusPage extends AppCompatActivity implements TaskSelectorAdapter.TaskSelectionListener {
    private RadioButton radioFullyCleaned;
    private EditText editTextNotes;
    private Button btnSaveDraft, btnCompleteTask;
    private CardView photoUploadCard;
    private RecyclerView taskSelectorRecyclerView;
    private TaskSelectorAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bin_status_page);

        // Initialize views
        initializeViews();
        setupToolbar();
        setupTaskSelector();
        setupClickListeners();
    }

    private void initializeViews() {
        radioFullyCleaned = findViewById(R.id.radioFullyCleaned);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        photoUploadCard = findViewById(R.id.photoUploadCard);
        taskSelectorRecyclerView = findViewById(R.id.taskSelectorRecyclerView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupTaskSelector() {
        // Create sample tasks (replace with real data)
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("#T2024-156", "Main Street Corner", "Bin cleaning and maintenance required", "In Progress", "0.3 km away"));
        tasks.add(new Task("#T2024-157", "Central Park", "Regular waste collection needed", "Pending", "0.8 km away"));
        tasks.add(new Task("#T2024-158", "Market Square", "Bin overflow reported", "In Progress", "1.2 km away"));
        tasks.add(new Task("#T2024-159", "Beach Front", "Weekly maintenance check", "Pending", "1.5 km away"));

        // Setup RecyclerView
        taskAdapter = new TaskSelectorAdapter(this, tasks, this);
        taskSelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskSelectorRecyclerView.setAdapter(taskAdapter);
    }

    private void setupClickListeners() {
        btnSaveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateBinStatusPage.this, "Draft saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateBinStatusPage.this, "Task completed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        photoUploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateBinStatusPage.this, "Photo upload coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskSelected(Task task, int position) {
        // Update UI based on selected task
        Toast.makeText(this, "Selected task: " + task.getTaskId(), Toast.LENGTH_SHORT).show();
    }
} 
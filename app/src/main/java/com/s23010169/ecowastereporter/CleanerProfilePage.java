package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;

public class CleanerProfilePage extends AppCompatActivity {
    private TextView cleanerName, emailText, phoneText, addressText;
    private TextView tasksCompletedCount, rating, experienceYears;
    private MaterialButton editProfileButton;
    private MaterialButton signOutButton;
    private CleanerDatabaseHelper databaseHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_profile_page);

        // Initialize database helper
        databaseHelper = new CleanerDatabaseHelper(this);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        initializeViews();
        setupToolbar();
        loadCleanerData();
        setupClickListeners();
    }

    private void initializeViews() {
        cleanerName = findViewById(R.id.cleanerName);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        addressText = findViewById(R.id.addressText);
        tasksCompletedCount = findViewById(R.id.tasksCompletedCount);
        rating = findViewById(R.id.rating);
        experienceYears = findViewById(R.id.experienceYears);
        editProfileButton = findViewById(R.id.editProfileButton);
        signOutButton = findViewById(R.id.signOutButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void loadCleanerData() {
        if (userEmail != null) {
            Cleaner cleaner = databaseHelper.getCleanerByEmail(userEmail);
            if (cleaner != null) {
                cleanerName.setText(cleaner.getName());
                emailText.setText(cleaner.getEmail());
                phoneText.setText(cleaner.getPhone());
                addressText.setText(cleaner.getArea());
                
                // Set actual values from the database
                tasksCompletedCount.setText(String.valueOf(cleaner.getTasksCompleted()));
                rating.setText(String.format("%.1f", cleaner.getRating()));
                experienceYears.setText(cleaner.getExperience());
            }
        }
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit profile functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        signOutButton.setOnClickListener(v -> {
            // Sign out: go to CleanerLoginPage and clear activity stack
            Intent intent = new Intent(this, CleanerLoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
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
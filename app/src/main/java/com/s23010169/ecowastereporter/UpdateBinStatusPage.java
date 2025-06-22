package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UpdateBinStatusPage extends AppCompatActivity {
    private RadioButton radioFullyCleaned;
    private EditText editTextNotes;
    private Button btnSaveDraft, btnCompleteTask;
    private CardView photoUploadCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bin_status_page);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        radioFullyCleaned = findViewById(R.id.radioFullyCleaned);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        photoUploadCard = findViewById(R.id.photoUploadCard);
    }

    private void setupClickListeners() {
        btnSaveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement draft saving functionality
                Toast.makeText(UpdateBinStatusPage.this, "Draft saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement task completion functionality
                Toast.makeText(UpdateBinStatusPage.this, "Task completed", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous screen
            }
        });

        photoUploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement photo upload functionality
                Toast.makeText(UpdateBinStatusPage.this, "Photo upload coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ReportWastePage extends AppCompatActivity {
    private CardView photoContainer;
    private EditText wasteTypeInput;
    private EditText locationInput;
    private EditText descriptionInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_waste_page);

        // Initialize views
        photoContainer = findViewById(R.id.photoContainer);
        wasteTypeInput = findViewById(R.id.wasteTypeInput);
        locationInput = findViewById(R.id.locationInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        submitButton = findViewById(R.id.submitButton);

        // Set click listeners
        photoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement photo selection
                Toast.makeText(ReportWastePage.this, "Photo selection coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement report submission
                if (validateInputs()) {
                    submitReport();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (wasteTypeInput.getText().toString().trim().isEmpty()) {
            wasteTypeInput.setError("Please select waste type");
            return false;
        }
        if (locationInput.getText().toString().trim().isEmpty()) {
            locationInput.setError("Please enter location");
            return false;
        }
        return true;
    }

    private void submitReport() {
        // TODO: Implement actual report submission logic
        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Return to previous screen
    }
} 
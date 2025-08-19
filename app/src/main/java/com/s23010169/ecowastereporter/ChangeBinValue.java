package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.s23010169.ecowastereporter.models.Bin;
import com.s23010169.ecowastereporter.models.DatabaseHelper;

public class ChangeBinValue extends AppCompatActivity {
    private Spinner binIdInput;
    private Spinner statusSpinner;
    private MaterialButton checkBinButton;
    private MaterialButton updateButton;
    private MaterialButton cancelButton;
    private DatabaseHelper databaseHelper;
    private List<Bin> allBins;
    
    // Views for bin information display
    private LinearLayout binInfoLayout;
    private TextView binIdDisplay;
    private TextView binLocationDisplay;
    private TextView binCurrentStatusDisplay;
    private TextView binFillLevelDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bin_value);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupToolbar();
        setupSpinner();
        loadAllBins();
        setupBinSelection();
        setupClickListeners();
    }

    private void initializeViews() {
        binIdInput = findViewById(R.id.binIdInput);
        statusSpinner = findViewById(R.id.statusSpinner);
        checkBinButton = findViewById(R.id.checkBinButton);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancelButton);
        
        // Initialize bin info display views
        binInfoLayout = findViewById(R.id.binInfoLayout);
        binIdDisplay = findViewById(R.id.binIdDisplay);
        binLocationDisplay = findViewById(R.id.binLocationDisplay);
        binCurrentStatusDisplay = findViewById(R.id.binCurrentStatusDisplay);
        binFillLevelDisplay = findViewById(R.id.binFillLevelDisplay);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Bin Status");
        }
    }

    private void setupSpinner() {
        // Create array of bin status options
        String[] statusOptions = {"Empty", "Half Full", "Full", "Overflowing"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }

    private void loadAllBins() {
        try {
            allBins = databaseHelper.getAllBins();
            if (allBins == null) {
                allBins = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            allBins = new ArrayList<>();
            Toast.makeText(this, "Error loading bins: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBinSelection() {
        try {
            // Create adapter for bin selection
            List<String> binOptions = new ArrayList<>();
            if (allBins != null) {
                for (Bin bin : allBins) {
                    if (bin != null) {
                        binOptions.add("Bin " + bin.getId() + " - " + bin.getLocation() + " (" + bin.getStatus() + ")");
                    }
                }
            }
            
            if (binOptions.isEmpty()) {
                binOptions.add("No bins available");
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, binOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binIdInput.setAdapter(adapter);
            
            // Handle bin selection
            binIdInput.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 && position < allBins.size() && allBins.get(position) != null) {
                        Bin selectedBin = allBins.get(position);
                        displayBinInfo(selectedBin);
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    // Do nothing
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up bin selection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        checkBinButton.setOnClickListener(v -> checkBin());
        updateButton.setOnClickListener(v -> updateBinStatus());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void checkBin() {
        int selectedPosition = binIdInput.getSelectedItemPosition();
        
        if (selectedPosition < 0 || selectedPosition >= allBins.size()) {
            Toast.makeText(this, "Please select a bin", Toast.LENGTH_SHORT).show();
            return;
        }

        Bin selectedBin = allBins.get(selectedPosition);
        displayBinInfo(selectedBin);
    }

    private void displayBinInfo(Bin bin) {
        binIdDisplay.setText(String.valueOf(bin.getId()));
        binLocationDisplay.setText(bin.getLocation());
        binCurrentStatusDisplay.setText(bin.getStatus());
        binFillLevelDisplay.setText(bin.getFillPercentage() + "%");
        
        binInfoLayout.setVisibility(View.VISIBLE);
        
        // Set the current status in the spinner
        String currentStatus = bin.getStatus();
        String[] statusOptions = {"Empty", "Half Full", "Full", "Overflowing"};
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(currentStatus)) {
                statusSpinner.setSelection(i);
                break;
            }
        }
    }

    private void updateBinStatus() {
        int selectedPosition = binIdInput.getSelectedItemPosition();
        String newStatus = statusSpinner.getSelectedItem().toString();

        if (selectedPosition < 0 || selectedPosition >= allBins.size()) {
            Toast.makeText(this, "Please select a bin", Toast.LENGTH_SHORT).show();
            return;
        }

        Bin selectedBin = allBins.get(selectedPosition);

        // Update bin status
        boolean success = databaseHelper.updateBinStatus(selectedBin.getId(), newStatus);
        if (success) {
            Toast.makeText(this, "Bin status updated successfully", Toast.LENGTH_SHORT).show();
            // Refresh the bin list and displayed information
            loadAllBins();
            setupBinSelection();
            displayBinInfo(databaseHelper.getBinById(selectedBin.getId()));
        } else {
            Toast.makeText(this, "Failed to update bin status", Toast.LENGTH_SHORT).show();
        }
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

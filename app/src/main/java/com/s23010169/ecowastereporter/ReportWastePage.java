package com.s23010169.ecowastereporter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class ReportWastePage extends AppCompatActivity {
    private CardView photoContainer;
    private Spinner wasteTypeSpinner;
    private EditText locationInput;
    private EditText descriptionInput;
    private MaterialButton submitButton;
    private ImageButton backButton;
    private MaterialButton getCurrentLocationButton;
    private FloatingActionButton cameraFab;
    private AppBarLayout appBarLayout;
    private String selectedWasteType;
    private boolean isSubmitting = false;
    private List<String> photoUris = new ArrayList<>(); // Store photo URIs
    private TextView photoCountText; // To show number of photos added
    private static final int MIN_DESCRIPTION_LENGTH = 20;
    private static final int MIN_PHOTOS_REQUIRED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_waste_page);

        initializeViews();
        setupSpinner();
        setupClickListeners();
        setupAppBarBehavior();
    }

    private void initializeViews() {
        photoContainer = findViewById(R.id.photoContainer);
        wasteTypeSpinner = findViewById(R.id.wasteTypeSpinner);
        locationInput = findViewById(R.id.locationInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        getCurrentLocationButton = findViewById(R.id.getCurrentLocationButton);
        cameraFab = findViewById(R.id.cameraFab);
        appBarLayout = findViewById(R.id.appBarLayout);
        photoCountText = findViewById(R.id.photoCountText);
        
        // Set initial photo count
        updatePhotoCount();
    }

    private void updatePhotoCount() {
        if (photoCountText != null) {
            if (photoUris.isEmpty()) {
                photoCountText.setText("No photos added");
                photoCountText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            } else {
                photoCountText.setText(String.format("%d photo%s added", 
                    photoUris.size(), photoUris.size() == 1 ? "" : "s"));
                photoCountText.setTextColor(ContextCompat.getColor(this, R.color.green_500));
            }
        }
    }

    private void setupSpinner() {
        // Create a custom adapter with styled items
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, 
                R.layout.spinner_item, 
                getResources().getStringArray(R.array.waste_types)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                text.setTextSize(16);
                if (position == 0) {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setPadding(32, 16, 32, 16);
                if (position == 0) {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                } else {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                }
                return view;
            }
        };
        
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        wasteTypeSpinner.setAdapter(adapter);
        wasteTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedWasteType = null;
                } else {
                    selectedWasteType = parent.getItemAtPosition(position).toString();
                    animateSelection(view);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedWasteType = null;
            }
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        photoContainer.setOnClickListener(v -> showImagePickerOptions());
        cameraFab.setOnClickListener(v -> showImagePickerOptions());

        getCurrentLocationButton.setOnClickListener(v -> {
            // Animate the button text and icon
            getCurrentLocationButton.setEnabled(false);
            getCurrentLocationButton.setText("Fetching...");
            
            // TODO: Implement actual location fetching
            locationInput.setText("Fetching location...");
            locationInput.setError(null); // Clear any previous errors
            
            v.postDelayed(() -> {
                getCurrentLocationButton.setEnabled(true);
                getCurrentLocationButton.setText("Current");
                locationInput.setText("Current Location");
                showSnackbar("Location updated successfully");
            }, 1500);
        });

        submitButton.setOnClickListener(v -> {
            if (!isSubmitting && validateInputs()) {
                submitReport();
            }
        });

        // Add text change listener for description to show remaining characters
        descriptionInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                int currentLength = s.length();
                if (currentLength > 0 && currentLength < MIN_DESCRIPTION_LENGTH) {
                    descriptionInput.setError(String.format("Minimum %d characters required (%d more needed)", 
                        MIN_DESCRIPTION_LENGTH, MIN_DESCRIPTION_LENGTH - currentLength));
                } else {
                    descriptionInput.setError(null);
                }
            }
        });
    }

    private void setupAppBarBehavior() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percentage = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
            cameraFab.setScaleX(1 - percentage);
            cameraFab.setScaleY(1 - percentage);
        });
    }

    private void showImagePickerOptions() {
        // TODO: Implement image picker functionality
        // For now, just simulate adding a photo
        photoUris.add("dummy_uri");
        updatePhotoCount();
        showSnackbar("Photo added successfully");
    }

    private void animateSelection(View view) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(100)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                }
            })
            .start();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate waste type selection
        if (selectedWasteType == null) {
            showSnackbar("Please select a waste type");
            isValid = false;
        }

        // Validate location
        String location = locationInput.getText().toString().trim();
        if (location.isEmpty()) {
            locationInput.setError("Location is required");
            isValid = false;
        }

        // Validate description
        String description = descriptionInput.getText().toString().trim();
        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            descriptionInput.setError(String.format("Minimum %d characters required", MIN_DESCRIPTION_LENGTH));
            isValid = false;
        }

        // Validate photos
        if (photoUris.size() < MIN_PHOTOS_REQUIRED) {
            showSnackbar("Please add at least one photo");
            isValid = false;
        }

        return isValid;
    }

    private void submitReport() {
        if (isSubmitting) return;
        isSubmitting = true;

        // Show loading state
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        // Create report data
        String location = locationInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // TODO: Create a Report object and save to database
        // For now, simulate a network delay
        new android.os.Handler().postDelayed(() -> {
            isSubmitting = false;
            submitButton.setEnabled(true);
            submitButton.setText("Submit Report");

            // Show success message and navigate back
            showSnackbar("Report submitted successfully");
            new android.os.Handler().postDelayed(this::navigateToMyReports, 1000);
        }, 2000);
    }

    private void navigateToMyReports() {
        Intent intent = new Intent(this, MyReportPage.class);
        startActivity(intent);
        finish();
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isSubmitting) {
            showSnackbar("Please wait while submitting the report");
            return;
        }
        super.onBackPressed();
    }
} 
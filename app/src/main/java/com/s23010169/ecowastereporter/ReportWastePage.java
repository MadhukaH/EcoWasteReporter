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
    }

    private void setupAppBarBehavior() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percentage = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
            cameraFab.setScaleX(1 - percentage);
            cameraFab.setScaleY(1 - percentage);
        });
    }

    private void showImagePickerOptions() {
        // Animate the photo container
        photoContainer.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    photoContainer.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start();
                }
            })
            .start();

        // TODO: Implement actual image picker
        showSnackbar("Photo selection coming soon");
    }

    private void animateSelection(View view) {
        if (view != null) {
            view.animate()
                .alpha(0.7f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                            .alpha(1f)
                            .setDuration(100)
                            .start();
                    }
                })
                .start();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (selectedWasteType == null) {
            showSnackbar("Please select waste type");
            wasteTypeSpinner.animate()
                .translationX(20f)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        wasteTypeSpinner.animate()
                            .translationX(-20f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    wasteTypeSpinner.animate()
                                        .translationX(0f)
                                        .setDuration(100)
                                        .start();
                                }
                            })
                            .start();
                    }
                })
                .start();
            isValid = false;
        }

        String location = locationInput.getText().toString().trim();
        if (location.isEmpty() || location.equals("Fetching location...")) {
            locationInput.setError("Please enter location");
            locationInput.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            isValid = false;
        }

        return isValid;
    }

    private void submitReport() {
        isSubmitting = true;
        submitButton.setEnabled(false);
        
        // Show progress state
        String originalText = submitButton.getText().toString();
        submitButton.setText("Submitting...");
        
        // Simulate report submission
        submitButton.postDelayed(() -> {
            isSubmitting = false;
            submitButton.setEnabled(true);
            submitButton.setText(originalText);
            
            Snackbar snackbar = Snackbar.make(submitButton, 
                "Report submitted successfully!", 
                Snackbar.LENGTH_LONG);
            snackbar.setAction("View", v -> {
                // TODO: Navigate to report details
            });
            snackbar.show();
            
            // Clear form
            locationInput.setText("");
            descriptionInput.setText("");
            wasteTypeSpinner.setSelection(0);
            
            // Return to previous screen after a short delay
            submitButton.postDelayed(this::onBackPressed, 1500);
        }, 2000);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (!isSubmitting) {
            super.onBackPressed();
        }
    }
} 
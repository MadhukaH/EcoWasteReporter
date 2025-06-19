package com.s23010169.ecowastereporter;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SelectPage extends AppCompatActivity {
    private static final float SELECTED_ALPHA = 1.0f;
    private static final float UNSELECTED_ALPHA = 0.6f;
    private static final int ANIMATION_DURATION = 200;

    private Button citizenButton;
    private Button cleanerButton;
    private Button continueButton;
    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_page);
        
        initializeViews();
        setupClickListeners();
        
        // Restore state if available
        if (savedInstanceState != null) {
            selectedRole = savedInstanceState.getString("SELECTED_ROLE", "");
            updateButtonStates();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SELECTED_ROLE", selectedRole);
    }

    private void initializeViews() {
        citizenButton = findViewById(R.id.citizenButton);
        cleanerButton = findViewById(R.id.cleanerButton);
        continueButton = findViewById(R.id.continueButton);
        
        // Set initial states
        continueButton.setEnabled(false);
        citizenButton.setAlpha(SELECTED_ALPHA);
        cleanerButton.setAlpha(SELECTED_ALPHA);
    }

    private void setupClickListeners() {
        citizenButton.setOnClickListener(v -> selectRole("CITIZEN"));
        cleanerButton.setOnClickListener(v -> selectRole("CLEANER"));
        continueButton.setOnClickListener(v -> navigateToNextScreen());
    }

    private void selectRole(String role) {
        if (role.equals(selectedRole)) {
            return; // Already selected
        }

        selectedRole = role;
        updateButtonStates();
        animateButtonSelection();
        continueButton.setEnabled(true);
    }

    private void updateButtonStates() {
        boolean isCitizenSelected = "CITIZEN".equals(selectedRole);
        citizenButton.setAlpha(isCitizenSelected ? SELECTED_ALPHA : UNSELECTED_ALPHA);
        cleanerButton.setAlpha(isCitizenSelected ? UNSELECTED_ALPHA : SELECTED_ALPHA);
    }

    private void animateButtonSelection() {
        Button selectedButton = "CITIZEN".equals(selectedRole) ? citizenButton : cleanerButton;
        Button unselectedButton = "CITIZEN".equals(selectedRole) ? cleanerButton : citizenButton;

        animateButtonAlpha(selectedButton, SELECTED_ALPHA);
        animateButtonAlpha(unselectedButton, UNSELECTED_ALPHA);
    }

    private void animateButtonAlpha(View view, float targetAlpha) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), targetAlpha);
        alphaAnimator.setDuration(ANIMATION_DURATION);
        alphaAnimator.start();
    }

    private void navigateToNextScreen() {
        if (selectedRole.isEmpty()) {
            return;
        }

        Intent intent;
        if ("CITIZEN".equals(selectedRole)) {
            intent = new Intent(SelectPage.this, UserReg.class);
        } else {
            intent = new Intent(SelectPage.this, CleanerReg.class);
        }
        startActivity(intent);
    }
} 
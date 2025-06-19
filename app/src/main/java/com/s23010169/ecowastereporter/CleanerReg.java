package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class CleanerReg extends AppCompatActivity {
    // UI Components
    private TextInputLayout nameInputLayout, emailInputLayout, serviceIdInputLayout, passwordInputLayout;
    private EditText nameEditText, emailEditText, serviceIdEditText, passwordEditText;
    private Button registerButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_reg);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize TextInputLayouts
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        serviceIdInputLayout = findViewById(R.id.serviceIdInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        // Initialize EditTexts
        nameEditText = nameInputLayout.getEditText();
        emailEditText = emailInputLayout.getEditText();
        serviceIdEditText = serviceIdInputLayout.getEditText();
        passwordEditText = passwordInputLayout.getEditText();

        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> validateAndRegister());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void validateAndRegister() {
        // Reset any previous errors
        clearErrors();

        // Get input values and trim whitespace
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String serviceId = serviceIdEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate all fields
        if (!validateFields(name, email, serviceId, password)) {
            return;
        }

        // If all validations pass, proceed with registration
        performRegistration(name, email, serviceId, password);
    }

    private boolean validateFields(String name, String email, String serviceId, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError("Name is required");
            isValid = false;
        } else if (name.length() < 3) {
            nameInputLayout.setError("Name must be at least 3 characters");
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(serviceId)) {
            serviceIdInputLayout.setError("Service ID is required");
            isValid = false;
        } else if (serviceId.length() < 6) {
            serviceIdInputLayout.setError("Service ID must be at least 6 characters");
            isValid = false;
        } else {
            serviceIdInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            passwordInputLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            passwordInputLayout.setError("Password must contain letters, numbers, and special characters");
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        return isValid;
    }

    private boolean isPasswordStrong(String password) {
        // Password must contain at least one letter, one number, and one special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).*$";
        return password.matches(passwordPattern);
    }

    private void clearErrors() {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        serviceIdInputLayout.setError(null);
        passwordInputLayout.setError(null);
    }

    private void performRegistration(String name, String email, String serviceId, String password) {
        // Show loading state
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        // TODO: Implement actual registration logic here (e.g., API call, database operation)
        // For now, we'll simulate a registration process
        new android.os.Handler().postDelayed(() -> {
            // Registration successful
            showSuccessMessage();
            navigateToLogin();
        }, 2000); // Simulated 2-second delay
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        registerButton.setEnabled(true);
        registerButton.setText("REGISTER");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CleanerReg.this, CleanerLoginPage.class);
        startActivity(intent);
        finish(); // Close registration activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources, cancel any ongoing operations
    }
} 
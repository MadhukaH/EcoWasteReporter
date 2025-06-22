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
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;

public class CleanerReg extends AppCompatActivity {
    // UI Components
    private TextInputLayout nameInputLayout, emailInputLayout, areaInputLayout, experienceInputLayout, passwordInputLayout, phoneInputLayout;
    private EditText nameEditText, emailEditText, areaEditText, experienceEditText, passwordEditText, phoneEditText;
    private Button registerButton;
    private TextView loginLink;
    private CleanerDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_reg);

        // Initialize database helper
        databaseHelper = new CleanerDatabaseHelper(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize TextInputLayouts
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        areaInputLayout = findViewById(R.id.areaInputLayout);
        experienceInputLayout = findViewById(R.id.experienceInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);

        // Initialize EditTexts
        nameEditText = nameInputLayout.getEditText();
        emailEditText = emailInputLayout.getEditText();
        areaEditText = areaInputLayout.getEditText();
        experienceEditText = experienceInputLayout.getEditText();
        passwordEditText = passwordInputLayout.getEditText();
        phoneEditText = phoneInputLayout.getEditText();

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
        String area = areaEditText.getText().toString().trim();
        String experience = experienceEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Validate all fields
        if (!validateFields(name, email, area, experience, password, phone)) {
            return;
        }

        // If all validations pass, proceed with registration
        performRegistration(name, email, area, experience, password, phone);
    }

    private boolean validateFields(String name, String email, String area, String experience, String password, String phone) {
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
        } else if (databaseHelper.checkEmail(email)) {
            emailInputLayout.setError("Email already exists");
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(area)) {
            areaInputLayout.setError("Area is required");
            isValid = false;
        } else {
            areaInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(experience)) {
            experienceInputLayout.setError("Experience is required");
            isValid = false;
        } else {
            experienceInputLayout.setError(null);
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

        if (TextUtils.isEmpty(phone)) {
            phoneInputLayout.setError("Phone number is required");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneInputLayout.setError("Please enter a valid phone number");
            isValid = false;
        } else {
            phoneInputLayout.setError(null);
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
        areaInputLayout.setError(null);
        experienceInputLayout.setError(null);
        passwordInputLayout.setError(null);
        phoneInputLayout.setError(null);
    }

    private void performRegistration(String name, String email, String area, String experience, String password, String phone) {
        // Show loading state
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        // Add cleaner to database
        long result = databaseHelper.addCleaner(name, email, password, phone, area, experience);

        if (result != -1) {
            // Registration successful
            showSuccessMessage();
            navigateToCleanerHomePage(email);
        } else {
            // Registration failed
            showError("Registration failed. Please try again.");
        }
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
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

    private void navigateToCleanerHomePage(String email) {
        Intent intent = new Intent(CleanerReg.this, CleanerHomePage.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish(); // Close registration activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources, cancel any ongoing operations
    }
} 
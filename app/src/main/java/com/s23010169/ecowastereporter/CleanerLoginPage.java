package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;

public class CleanerLoginPage extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private View registerLink, forgotPasswordText;
    private CleanerDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_login);

        // Initialize database helper
        databaseHelper = new CleanerDatabaseHelper(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize TextInputLayouts
        emailLayout = findViewById(R.id.emailInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);

        // Initialize EditTexts
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        // Initialize buttons and clickable views
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(this);
        registerLink.setOnClickListener(this);
        forgotPasswordText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {
            handleLogin();
        } else if (v.getId() == R.id.registerLink) {
            navigateToRegister();
        } else if (v.getId() == R.id.forgotPasswordText) {
            navigateToForgotPassword();
        }
    }

    private void handleLogin() {
        // Reset errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Get input values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Disable login button and show loading state
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Check credentials in database
        if (databaseHelper.checkCleaner(email, password)) {
            // Login successful
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToCleanerHomePage();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            loginButton.setEnabled(true);
            loginButton.setText("LOGIN");
        }
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private void navigateToCleanerHomePage() {
        Intent intent = new Intent(this, CleanerHomePage.class);
        intent.putExtra("email", emailInput.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, CleanerReg.class);
        startActivity(intent);
        finish();
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordPage.class);
        startActivity(intent);
    }
} 
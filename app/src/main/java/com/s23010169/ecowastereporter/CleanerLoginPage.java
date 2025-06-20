package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CleanerLoginPage extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout serviceIdLayout, passwordLayout;
    private TextInputEditText serviceIdInput, passwordInput;
    private MaterialButton loginButton;
    private View registerLink, forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_login);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize TextInputLayouts
        serviceIdLayout = findViewById(R.id.serviceIdInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);

        // Initialize EditTexts
        serviceIdInput = findViewById(R.id.serviceIdInput);
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
        serviceIdLayout.setError(null);
        passwordLayout.setError(null);

        // Get input values
        String serviceId = serviceIdInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(serviceId, password)) {
            return;
        }

        // TODO: Implement actual login logic here
        // For now, just show a toast message
        Toast.makeText(this, "Login functionality to be implemented", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs(String serviceId, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(serviceId)) {
            serviceIdLayout.setError("Service ID is required");
            isValid = false;
        } else if (serviceId.length() < 4) {
            serviceIdLayout.setError("Service ID must be at least 4 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
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
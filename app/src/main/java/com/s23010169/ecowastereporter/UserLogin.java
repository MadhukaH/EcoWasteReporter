package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.s23010169.ecowastereporter.models.CitizenDatabaseHelper;

// This screen lets a citizen log in with email and password.
public class UserLogin extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private CardView googleSignIn, facebookSignIn, twitterSignIn;
    private View forgotPasswordText, registerText;
    private CitizenDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize database helper
        databaseHelper = new CitizenDatabaseHelper(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize TextInputLayouts
        emailLayout = findViewById(R.id.emailInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);

        // Initialize EditTexts
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Initialize buttons and clickable views
        loginButton = findViewById(R.id.loginButton);
        googleSignIn = findViewById(R.id.googleSignIn);
        facebookSignIn = findViewById(R.id.facebookSignIn);
        twitterSignIn = findViewById(R.id.twitterSignIn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        registerText = findViewById(R.id.registerText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        facebookSignIn.setOnClickListener(this);
        twitterSignIn.setOnClickListener(this);
        forgotPasswordText.setOnClickListener(this);
        registerText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {
            handleLogin();
        } else if (v.getId() == R.id.googleSignIn) {
            showFeatureComingSoon("Google Sign In");
        } else if (v.getId() == R.id.facebookSignIn) {
            showFeatureComingSoon("Facebook Sign In");
        } else if (v.getId() == R.id.twitterSignIn) {
            showFeatureComingSoon("X (Twitter) Sign In");
        } else if (v.getId() == R.id.forgotPasswordText) {
            navigateToForgotPassword();
        } else if (v.getId() == R.id.registerText) {
            navigateToRegister();
        }
    }

    private void handleLogin() {
        // Reset errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Get input values
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Check credentials in database
        if (databaseHelper.checkCitizen(email, password)) {
            // Login successful
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToPeopleHomePage();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
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

    private void navigateToPeopleHomePage() {
        Intent intent = new Intent(this, PeopleHomePage.class);
        intent.putExtra("email", emailEditText.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, UserReg.class);
        startActivity(intent);
        finish();
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordPage.class);
        startActivity(intent);
    }

    private void showFeatureComingSoon(String feature) {
        Toast.makeText(this, feature + " coming soon", Toast.LENGTH_SHORT).show();
    }
} 
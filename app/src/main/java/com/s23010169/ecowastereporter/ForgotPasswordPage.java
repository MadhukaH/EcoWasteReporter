package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// This screen lets users request a password reset by entering their email address.
public class ForgotPasswordPage extends AppCompatActivity {
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInput;
    private Button sendOtpButton;
    private TextView backToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailInput = findViewById(R.id.emailInput);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        backToSignIn = findViewById(R.id.backToSignIn);

        // Set up click listeners
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()) {
                    // TODO: Implement OTP sending logic
                    Toast.makeText(ForgotPasswordPage.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to VerificationCodePage
                    Intent intent = new Intent(ForgotPasswordPage.this, VerificationCodePage.class);
                    startActivity(intent);
                }
            }
        });

        backToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous screen
            }
        });

        // Add text change listener for real-time validation
        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail();
                }
            }
        });
    }

    private boolean validateEmail() {
        String email = emailInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            return false;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            return false;
        }
        
        emailInputLayout.setError(null);
        return true;
    }
} 
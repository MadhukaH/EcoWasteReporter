package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CleanerLoginPage extends AppCompatActivity {
    private EditText serviceIdInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_login);

        // Initialize views
        serviceIdInput = findViewById(R.id.serviceIdInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Set click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceId = serviceIdInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (serviceId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CleanerLoginPage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Implement login authentication
                // For now, just show a toast message
                Toast.makeText(CleanerLoginPage.this, "Login functionality to be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CleanerLoginPage.this, CleanerReg.class));
                finish();
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement forgot password functionality
                Toast.makeText(CleanerLoginPage.this, "Forgot password functionality to be implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
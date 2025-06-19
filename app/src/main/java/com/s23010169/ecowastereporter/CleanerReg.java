package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CleanerReg extends AppCompatActivity {
    private EditText nameEditText, emailEditText, serviceIdEditText, passwordEditText;
    private Button registerButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_reg);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        serviceIdEditText = findViewById(R.id.serviceIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        // Register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String serviceId = serviceIdEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate inputs
                if (name.isEmpty() || email.isEmpty() || serviceId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CleanerReg.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Implement registration logic here
                Toast.makeText(CleanerReg.this, "Registration functionality to be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        // Login link click listener
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CleanerReg.this, UserLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }
} 
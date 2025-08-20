package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// This screen lets users enter a 4-digit verification code to reset their password.
public class VerificationCodePage extends AppCompatActivity {
    private EditText[] otpEditTexts;
    private Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        // Initialize OTP EditTexts
        otpEditTexts = new EditText[4];
        otpEditTexts[0] = findViewById(R.id.etOtp1);
        otpEditTexts[1] = findViewById(R.id.etOtp2);
        otpEditTexts[2] = findViewById(R.id.etOtp3);
        otpEditTexts[3] = findViewById(R.id.etOtp4);

        btnVerify = findViewById(R.id.btnVerify);

        setupOtpInputs();
        setupVerifyButton();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int currentIndex = i;
            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpEditTexts.length - 1) {
                        otpEditTexts[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        otpEditTexts[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupVerifyButton() {
        btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        StringBuilder otp = new StringBuilder();
        boolean isComplete = true;

        for (EditText editText : otpEditTexts) {
            String digit = editText.getText().toString();
            if (digit.isEmpty()) {
                isComplete = false;
                editText.setError("Required");
            }
            otp.append(digit);
        }

        if (!isComplete) {
            Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Verify OTP with your backend
        // For demo purposes, we'll assume "1234" is the correct OTP
        if (otp.toString().equals("0000")) {
            // Navigate to New Password page
            Intent intent = new Intent(this, NewPasswordPage.class);
            startActivity(intent);
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
} 
package com.s23010169.ecowastereporter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VerificationCodePage extends AppCompatActivity {
    private EditText[] otpFields;
    private Button verifyButton;
    private TextView resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        // Initialize OTP fields
        otpFields = new EditText[4];
        otpFields[0] = findViewById(R.id.otp_field_1);
        otpFields[1] = findViewById(R.id.otp_field_2);
        otpFields[2] = findViewById(R.id.otp_field_3);
        otpFields[3] = findViewById(R.id.otp_field_4);

        verifyButton = findViewById(R.id.verify_button);
        resendButton = findViewById(R.id.resend_button);

        // Add click listener for verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });

        // Add click listener for resend button
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
    }

    private void verifyCode() {
        // TODO: Implement verification logic
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }
        // Add verification logic here
    }

    private void resendCode() {
        // TODO: Implement resend logic
    }
} 
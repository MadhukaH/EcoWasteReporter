package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

// This screen shows cleaner profile information, stats, and lets them update their profile photo.
public class CleanerProfilePage extends AppCompatActivity {
    private TextView cleanerName, emailText, phoneText, addressText;
    private TextView tasksCompletedCount, rating, experienceYears;
    private MaterialButton binStatusButton;
    private MaterialButton editProfileButton;
    private MaterialButton signOutButton;
    private CleanerDatabaseHelper databaseHelper;
    private String userEmail;
    private CircleImageView profileImage;
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    saveImageToInternalStorage(imageUri);
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_profile_page);

        // Initialize database helper
        databaseHelper = new CleanerDatabaseHelper(this);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        initializeViews();
        setupToolbar();
        loadCleanerData();
        setupClickListeners();
    }

    private void initializeViews() {
        cleanerName = findViewById(R.id.cleanerName);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        addressText = findViewById(R.id.addressText);
        tasksCompletedCount = findViewById(R.id.tasksCompletedCount);
        rating = findViewById(R.id.rating);
        experienceYears = findViewById(R.id.experienceYears);
        binStatusButton = findViewById(R.id.binStatusButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        signOutButton = findViewById(R.id.signOutButton);
        profileImage = findViewById(R.id.profileImage);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void loadCleanerData() {
        if (userEmail != null) {
            Cleaner cleaner = databaseHelper.getCleanerByEmail(userEmail);
            if (cleaner != null) {
                cleanerName.setText(cleaner.getName());
                emailText.setText(cleaner.getEmail());
                phoneText.setText(cleaner.getPhone());
                addressText.setText(cleaner.getArea());
                
                // Set actual values from the database
                tasksCompletedCount.setText(String.valueOf(cleaner.getTasksCompleted()));
                rating.setText(String.format("%.1f", cleaner.getRating()));
                experienceYears.setText(cleaner.getExperience());
                // Load profile photo if exists
                String profilePhotoPath = cleaner.getProfilePhoto();
                if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                    File photoFile = new File(profilePhotoPath);
                    if (photoFile.exists()) {
                        profileImage.setImageURI(Uri.fromFile(photoFile));
                    }
                }
            }
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            // Create a unique filename
            String fileName = "profile_photo_" + System.currentTimeMillis() + ".jpg";
            File photoFile = new File(getFilesDir(), fileName);
            // Copy the selected image to internal storage
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(photoFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            // Save the file path to database
            String photoPath = photoFile.getAbsolutePath();
            int result = databaseHelper.updateProfilePhoto(userEmail, photoPath);
            if (result > 0) {
                // Update the image view
                profileImage.setImageURI(Uri.fromFile(photoFile));
                Toast.makeText(this, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving profile photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
        binStatusButton.setOnClickListener(v -> {
            // Navigate to ChangeBinValue activity
            Intent intent = new Intent(this, ChangeBinValue.class);
            startActivity(intent);
        });
        editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit profile functionality coming soon", Toast.LENGTH_SHORT).show();
        });
        signOutButton.setOnClickListener(v -> {
            // Sign out: go to CleanerLoginPage and clear activity stack
            Intent intent = new Intent(this, CleanerLoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 
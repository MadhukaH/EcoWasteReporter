package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

// This screen shows user profile information, stats, and lets users change profile photo and password.
public class ProfilePage extends AppCompatActivity {
    private TextView userName, userLevel, reportsCount, resolvedCount, pointsCount;
    private MaterialCardView changePasswordLayout, performanceLayout, rewardsLayout;
    private MaterialButton signOutButton;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CircleImageView profileImage;
    private String userEmail;
    private com.s23010169.ecowastereporter.models.CitizenDatabaseHelper databaseHelper;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    // Save the image to internal storage and database
                    saveImageToInternalStorage(imageUri);
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        // Initialize database helper
        databaseHelper = new com.s23010169.ecowastereporter.models.CitizenDatabaseHelper(this);

        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();
        
        // Set click listeners
        setClickListeners();
        
        // Load user data
        loadUserData();
    }

    private void initializeViews() {
        userName = findViewById(R.id.userName);
        userLevel = findViewById(R.id.userLevel);
        reportsCount = findViewById(R.id.reportsCount);
        resolvedCount = findViewById(R.id.resolvedCount);
        pointsCount = findViewById(R.id.pointsCount);
        
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        performanceLayout = findViewById(R.id.performanceLayout);
        rewardsLayout = findViewById(R.id.rewardsLayout);
        signOutButton = findViewById(R.id.signOutButton);
        
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImage);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setClickListeners() {
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });

        changePasswordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, ForgotPasswordPage.class);
            startActivity(intent);
        });

        performanceLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, PerformanceSummaryPage.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        rewardsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, LevelsRewardsPage.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        signOutButton.setOnClickListener(v -> {
            // TODO: Implement sign out logic
            Intent intent = new Intent(ProfilePage.this, SelectPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        if (userEmail != null) {
            com.s23010169.ecowastereporter.models.Citizen citizen = databaseHelper.getCitizenByEmail(userEmail);
            if (citizen != null) {
                userName.setText(citizen.getName());
                
                // Load profile photo if exists
                String profilePhotoPath = citizen.getProfilePhoto();
                if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                    File photoFile = new File(profilePhotoPath);
                    if (photoFile.exists()) {
                        profileImage.setImageURI(Uri.fromFile(photoFile));
                    }
                }
            } else {
                userName.setText("Unknown User");
            }
        } else {
            userName.setText("Unknown User");
        }
        // Populate dynamic stats if available
        try {
            com.s23010169.ecowastereporter.models.CitizenDatabaseHelper db = new com.s23010169.ecowastereporter.models.CitizenDatabaseHelper(this);
            int level = db.getUserLevel(userEmail);
            int points = db.getUserPoints(userEmail);
            userLevel.setText("Level " + level);
            pointsCount.setText(String.valueOf(points));
            // Reports counts
            com.s23010169.ecowastereporter.models.ReportDatabaseHelper reportDb = new com.s23010169.ecowastereporter.models.ReportDatabaseHelper(this);
            int totalReports = reportDb.getTotalReportsCount();
            int resolvedReports = reportDb.getResolvedReportsCount();
            reportsCount.setText(String.valueOf(totalReports));
            resolvedCount.setText(String.valueOf(resolvedReports));
        } catch (Exception ignored) {
            userLevel.setText("Level 1");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 
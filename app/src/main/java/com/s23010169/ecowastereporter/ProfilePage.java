package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ProfilePage extends AppCompatActivity {
    private TextView userName, userLevel, reportsCount, resolvedCount, pointsCount;
    private MaterialCardView changePasswordLayout, performanceLayout, rewardsLayout;
    private MaterialButton signOutButton;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CircleImageView profileImage;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null) {
                    profileImage.setImageURI(imageUri);
                    // TODO: Upload the image to your backend server or save it locally
                    Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
            startActivity(intent);
        });

        rewardsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, LevelsRewardsPage.class);
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
        // TODO: Load actual user data from database/preferences
        userName.setText("Capt Price");
        userLevel.setText("Level 3 • Eco Warrior");
        reportsCount.setText("12");
        resolvedCount.setText("8");
        pointsCount.setText("350");
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
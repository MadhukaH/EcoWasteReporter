package com.s23010169.ecowastereporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.s23010169.ecowastereporter.models.Citizen;
import com.s23010169.ecowastereporter.models.CitizenDatabaseHelper;

import java.io.File;

public class PeopleHomePage extends AppCompatActivity implements View.OnClickListener {
    private MaterialCardView reportWasteCard, viewMapCard, myReportsCard, recyclingTipsCard;
    private ExtendedFloatingActionButton reportFab;
    private NestedScrollView scrollView;
    private ShapeableImageView profileImage;
    private TextView welcomeText;
    private TextView homeReportsCount;
    private TextView homeLevelName;
    private CitizenDatabaseHelper databaseHelper;
    private String userEmail;

    @Override
    public void onBackPressed() {
        // Prevent going back to login/registration screens
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_home_page);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");
        
        // Initialize database helper
        databaseHelper = new CitizenDatabaseHelper(this);

        initializeViews();
        setupClickListeners();
        setupScrollBehavior();
        displayUserName();
        loadProfilePhoto();
        updateStatsWidgets();
    }

    private void initializeViews() {
        reportWasteCard = findViewById(R.id.reportWasteCard);
        viewMapCard = findViewById(R.id.viewMapCard);
        myReportsCard = findViewById(R.id.myReportsCard);
        recyclingTipsCard = findViewById(R.id.recyclingTipsCard);
        reportFab = findViewById(R.id.reportFab);
        scrollView = findViewById(R.id.scrollView);
        profileImage = findViewById(R.id.profileImage);
        welcomeText = findViewById(R.id.welcomeText);
        homeReportsCount = findViewById(R.id.homeReportsCount);
        homeLevelName = findViewById(R.id.homeLevelName);
    }

    private void displayUserName() {
        if (userEmail != null) {
            Citizen citizen = databaseHelper.getCitizenByEmail(userEmail);
            if (citizen != null) {
                welcomeText.setText("Hello, " + citizen.getName() + "! 👋");
            }
        }
    }

    private void loadProfilePhoto() {
        if (userEmail != null) {
            Citizen citizen = databaseHelper.getCitizenByEmail(userEmail);
            if (citizen != null) {
                String profilePhotoPath = citizen.getProfilePhoto();
                if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                    File photoFile = new File(profilePhotoPath);
                    if (photoFile.exists()) {
                        profileImage.setImageURI(Uri.fromFile(photoFile));
                    }
                }
            }
        }
    }

    private void updateStatsWidgets() {
        try {
            // Reports count (total reports for now)
            com.s23010169.ecowastereporter.models.ReportDatabaseHelper reportDb = new com.s23010169.ecowastereporter.models.ReportDatabaseHelper(this);
            int totalReports = reportDb.getTotalReportsCount();
            if (homeReportsCount != null) {
                homeReportsCount.setText(String.valueOf(totalReports));
            }

            // Level name based on user's level
            if (userEmail != null) {
                int level = databaseHelper.getUserLevel(userEmail);
                String levelName;
                switch (level) {
                    default:
                    case 1: levelName = getString(R.string.level_1_title); break;
                    case 2: levelName = getString(R.string.level_2_title); break;
                    case 3: levelName = getString(R.string.level_3_title); break;
                    case 4: levelName = getString(R.string.level_4_title); break;
                    case 5: levelName = getString(R.string.level_5_title); break;
                    case 6: levelName = getString(R.string.level_6_title); break;
                }
                if (homeLevelName != null) {
                    homeLevelName.setText(levelName);
                }
            }
        } catch (Exception ignored) { }
    }

    private void setupClickListeners() {
        reportWasteCard.setOnClickListener(this);
        viewMapCard.setOnClickListener(this);
        myReportsCard.setOnClickListener(this);
        recyclingTipsCard.setOnClickListener(this);
        reportFab.setOnClickListener(this);
        
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(PeopleHomePage.this, ProfilePage.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });
    }

    private void setupScrollBehavior() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) 
            (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY > oldScrollY && reportFab.isExtended()) {
                    reportFab.shrink();
                }
                if (scrollY < oldScrollY && !reportFab.isExtended()) {
                    reportFab.extend();
                }
                if (scrollY == 0) {
                    reportFab.extend();
                }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reportWasteCard || id == R.id.reportFab) {
            Intent intent = new Intent(PeopleHomePage.this, ReportWastePage.class);
            startActivity(intent);
        } else if (id == R.id.viewMapCard) {
            Intent intent = new Intent(PeopleHomePage.this, NearbyBinsPage.class);
            startActivity(intent);
        } else if (id == R.id.myReportsCard) {
            Intent intent = new Intent(PeopleHomePage.this, MyReportPage.class);
            startActivity(intent);
        } else if (id == R.id.recyclingTipsCard) {
            Intent intent = new Intent(PeopleHomePage.this, RecyclingTipsPage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile photo when returning from ProfilePage
        loadProfilePhoto();
        updateStatsWidgets();
    }

    private void showFeatureMessage(String feature) {
        Toast.makeText(this, feature + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
} 
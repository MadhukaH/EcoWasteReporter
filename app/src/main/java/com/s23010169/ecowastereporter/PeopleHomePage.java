package com.s23010169.ecowastereporter;

import android.content.Intent;
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

public class PeopleHomePage extends AppCompatActivity implements View.OnClickListener {
    private MaterialCardView reportWasteCard, viewMapCard, myReportsCard, recyclingTipsCard, performanceCard;
    private ExtendedFloatingActionButton reportFab;
    private NestedScrollView scrollView;
    private ShapeableImageView profileImage;
    private TextView welcomeText;
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
    }

    private void initializeViews() {
        reportWasteCard = findViewById(R.id.reportWasteCard);
        viewMapCard = findViewById(R.id.viewMapCard);
        myReportsCard = findViewById(R.id.myReportsCard);
        recyclingTipsCard = findViewById(R.id.recyclingTipsCard);
        performanceCard = findViewById(R.id.performanceCard);
        reportFab = findViewById(R.id.reportFab);
        scrollView = findViewById(R.id.scrollView);
        profileImage = findViewById(R.id.profileImage);
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void displayUserName() {
        if (userEmail != null) {
            Citizen citizen = databaseHelper.getCitizenByEmail(userEmail);
            if (citizen != null) {
                welcomeText.setText("Hello, " + citizen.getName() + "! 👋");
            }
        }
    }

    private void setupClickListeners() {
        reportWasteCard.setOnClickListener(this);
        viewMapCard.setOnClickListener(this);
        myReportsCard.setOnClickListener(this);
        recyclingTipsCard.setOnClickListener(this);
        performanceCard.setOnClickListener(this);
        reportFab.setOnClickListener(this);
        
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(PeopleHomePage.this, ProfilePage.class);
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
        } else if (id == R.id.performanceCard) {
            Intent intent = new Intent(PeopleHomePage.this, PerformanceSummaryPage.class);
            startActivity(intent);
        }
    }

    private void showFeatureMessage(String feature) {
        Toast.makeText(this, feature + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
} 
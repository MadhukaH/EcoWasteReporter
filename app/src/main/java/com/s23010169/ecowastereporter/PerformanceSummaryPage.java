package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.s23010169.ecowastereporter.adapters.ReportAdapter;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import com.s23010169.ecowastereporter.models.CitizenDatabaseHelper;

import java.util.List;

public class PerformanceSummaryPage extends AppCompatActivity {
    private TextView totalReportsCount;
    private TextView resolvedReportsCount;
    private TextView impactScore;
    private TextView currentLevel;
    private TextView pointsToNextLevel;
    private LinearProgressIndicator levelProgress;
    private RecyclerView recentActivityRecyclerView;
    private ReportDatabaseHelper reportDatabaseHelper;
    private CitizenDatabaseHelper citizenDb;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_summary);

        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();

        // Initialize database helpers
        reportDatabaseHelper = new ReportDatabaseHelper(this);
        citizenDb = new CitizenDatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");

        // Load performance data
        loadPerformanceData();

        // Setup recent activity
        setupRecentActivity();
    }

    private void initializeViews() {
        totalReportsCount = findViewById(R.id.totalReportsCount);
        resolvedReportsCount = findViewById(R.id.resolvedReportsCount);
        impactScore = findViewById(R.id.impactScore);
        currentLevel = findViewById(R.id.currentLevel);
        pointsToNextLevel = findViewById(R.id.pointsToNextLevel);
        levelProgress = findViewById(R.id.levelProgress);
        recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void loadPerformanceData() {
        // Get total reports count
        int totalReports = reportDatabaseHelper.getTotalReportsCount();
        totalReportsCount.setText(String.valueOf(totalReports));

        // Get resolved reports count
        int resolvedReports = reportDatabaseHelper.getResolvedReportsCount();
        resolvedReportsCount.setText(String.valueOf(resolvedReports));

        // Calculate impact score (example: based on reports and resolution rate)
        int impact = calculateImpactScore(totalReports, resolvedReports);
        impactScore.setText(String.valueOf(impact));

        // Update level information
        updateLevelInfo(impact);

        // If user available, show their current points
        if (userEmail != null) {
            int points = citizenDb.getUserPoints(userEmail);
            // pointsCount exists only in ProfilePage, so here we enrich level text
            pointsToNextLevel.setText(pointsToNextLevel.getText() + " • " + points + " pts");
        }
    }

    private int calculateImpactScore(int totalReports, int resolvedReports) {
        // Example scoring system:
        // Base score from total reports
        int baseScore = totalReports * 10;
        
        // Bonus for resolved reports (50% bonus)
        int resolutionBonus = resolvedReports * 15;
        
        return baseScore + resolutionBonus;
    }

    private void updateLevelInfo(int impactScore) {
        // Example level thresholds
        String level;
        int progress;
        int pointsNeeded;

        if (impactScore < 100) {
            level = "Eco Rookie";
            progress = (impactScore * 100) / 100;
            pointsNeeded = 100 - impactScore;
        } else if (impactScore < 300) {
            level = "Eco Warrior";
            progress = ((impactScore - 100) * 100) / 200;
            pointsNeeded = 300 - impactScore;
        } else if (impactScore < 600) {
            level = "Eco Champion";
            progress = ((impactScore - 300) * 100) / 300;
            pointsNeeded = 600 - impactScore;
        } else {
            level = "Eco Master";
            progress = 100;
            pointsNeeded = 0;
        }

        currentLevel.setText(String.format("Current Level: %s", level));
        levelProgress.setProgress(progress);
        
        if (pointsNeeded > 0) {
            pointsToNextLevel.setText(String.format("%d points to next level", pointsNeeded));
        } else {
            pointsToNextLevel.setText("Maximum level reached!");
        }
    }

    private void setupRecentActivity() {
        // Get recent reports (limit to 5)
        List<Report> recentReports = reportDatabaseHelper.getRecentReports(5);
        
        // Setup RecyclerView
        ReportAdapter adapter = new ReportAdapter(this, recentReports);
        recentActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentActivityRecyclerView.setAdapter(adapter);
    }
} 
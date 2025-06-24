package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LevelsRewardsPage extends AppCompatActivity {
    private RecyclerView rewardsRecyclerView;
    private ProgressBar levelProgress;
    private TextView levelProgressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_rewards_page);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        levelProgress = findViewById(R.id.levelProgress);
        levelProgressText = findViewById(R.id.levelProgressText);
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView);
        
        // Setup RecyclerView
        rewardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set adapter for rewards list

        // TODO: Load user level and progress
        updateLevelProgress(3, 75, 100);
    }

    private void updateLevelProgress(int level, int current, int max) {
        levelProgress.setMax(max);
        levelProgress.setProgress(current);
        levelProgressText.setText(String.format("Level %d - %d/%d points", level, current, max));
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
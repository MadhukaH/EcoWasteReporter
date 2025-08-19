package com.s23010169.ecowastereporter;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.s23010169.ecowastereporter.adapters.LevelAdapter;
import com.s23010169.ecowastereporter.models.Level;
import com.s23010169.ecowastereporter.models.CitizenDatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class LevelsRewardsPage extends AppCompatActivity {
    private View levelInfoCard;
    private RecyclerView levelProgressionRecyclerView;
    private TextView levelTitle, currentXp, xpToNext, totalPoints, levelsCountLabel;
    private LinearProgressIndicator levelProgress;
    private LevelAdapter levelAdapter;
    private View redeem100Btn, redeem200Btn, redeem300Btn,
                 redeem400Btn, redeem500Btn, redeem600Btn, redeem700Btn, redeem800Btn, redeem900Btn,
                 redeem1000Btn, redeem1500Btn, redeem2000Btn;

    private CitizenDatabaseHelper citizenDb;
    private String userEmail;
    private String userPhone;
    private int userLevel = 1;
    private int currentXpValue = 0;
    private int totalPointsValue = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_rewards);

        initializeViews();
        citizenDb = new CitizenDatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");
        userPhone = getIntent().getStringExtra("phone");
        setupToolbar();
        loadUserData();
        setupLevelProgressionRecyclerView();
        animateProgress();
        setupRedemptionButtons();
    }

    private void initializeViews() {
        levelInfoCard = findViewById(R.id.levelInfoCard);
        levelProgressionRecyclerView = findViewById(R.id.levelProgressionRecyclerView);
        levelTitle = findViewById(R.id.levelTitle);
        currentXp = findViewById(R.id.currentXp);
        xpToNext = findViewById(R.id.xpToNext);
        totalPoints = findViewById(R.id.totalPoints);
        levelsCountLabel = findViewById(R.id.levelsCountLabel);
        levelProgress = findViewById(R.id.levelProgress);
        redeem100Btn = findViewById(R.id.redeem100Btn);
        redeem200Btn = findViewById(R.id.redeem200Btn);
        redeem300Btn = findViewById(R.id.redeem300Btn);
        redeem400Btn = findViewById(R.id.redeem400Btn);
        redeem500Btn = findViewById(R.id.redeem500Btn);
        redeem600Btn = findViewById(R.id.redeem600Btn);
        redeem700Btn = findViewById(R.id.redeem700Btn);
        redeem800Btn = findViewById(R.id.redeem800Btn);
        redeem900Btn = findViewById(R.id.redeem900Btn);
        redeem1000Btn = findViewById(R.id.redeem1000Btn);
        redeem1500Btn = findViewById(R.id.redeem1500Btn);
        redeem2000Btn = findViewById(R.id.redeem2000Btn);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); // Clear the title as we're using a custom TextView
        }
    }

    private void loadUserData() {
        // Load from DB: prefer phone, fallback to email
        if (userPhone != null && !userPhone.isEmpty()) {
            userLevel = citizenDb.getUserLevelByPhone(userPhone);
            currentXpValue = citizenDb.getUserCurrentXpByPhone(userPhone);
            totalPointsValue = citizenDb.getUserPointsByPhone(userPhone);
        } else if (userEmail != null && !userEmail.isEmpty()) {
            userLevel = citizenDb.getUserLevel(userEmail);
            currentXpValue = citizenDb.getUserCurrentXp(userEmail);
            totalPointsValue = citizenDb.getUserPoints(userEmail);
        }

        List<Level> levels = getLevels();
        Level currentLevel = getCurrentLevel();
        levelTitle.setText(currentLevel.getTitle());
        currentXp.setText(getString(R.string.current_xp, currentXpValue));

        int currentThreshold = currentLevel.getRequiredPoints();
        int nextThreshold = currentThreshold;
        String nextLevelName = null;
        for (Level l : levels) {
            if (l.getLevelNumber() == currentLevel.getLevelNumber() + 1) {
                nextThreshold = l.getRequiredPoints();
                nextLevelName = l.getTitle();
                break;
            }
        }
        int totalNeeded = Math.max(1, nextThreshold - currentThreshold);
        int gainedInLevel = Math.max(0, currentXpValue - currentThreshold);
        int needed = Math.max(0, nextThreshold - currentXpValue);
        if (nextLevelName != null) {
            TextView nextLevelLabel = findViewById(R.id.nextLevelLabel);
            TextView pointsToNextLevelLabel = findViewById(R.id.pointsToNextLevelLabel);
            if (nextLevelLabel != null) {
                nextLevelLabel.setText("Next Level: " + nextLevelName);
            }
            if (pointsToNextLevelLabel != null) {
                pointsToNextLevelLabel.setText("  |  " + needed + " points needed");
            }
        }
        xpToNext.setText(getString(R.string.xp_needed, needed));
        totalPoints.setText(String.valueOf(totalPointsValue));
        // points are shown in the header card
        if (levelsCountLabel != null) {
            levelsCountLabel.setText(levels.size() + " Levels");
        }

        // Set progress
        levelProgress.setMax(100);
        levelProgress.setProgress(0); // Start at 0 for animation
        // store in field for animateProgress to use
        this.currentXpValue = currentXpValue; // unchanged
        refreshRedemptionButtonsState();
    }

    private void setupRedemptionButtons() {
        if (redeem100Btn != null) {
            redeem100Btn.setOnClickListener(v -> attemptRedeem(100, 100));
        }
        if (redeem200Btn != null) {
            redeem200Btn.setOnClickListener(v -> attemptRedeem(200, 150));
        }
        if (redeem300Btn != null) {
            redeem300Btn.setOnClickListener(v -> attemptRedeem(300, 200));
        }
        if (redeem400Btn != null) {
            redeem400Btn.setOnClickListener(v -> attemptRedeem(400, 250));
        }
        if (redeem500Btn != null) {
            redeem500Btn.setOnClickListener(v -> attemptRedeem(500, 300));
        }
        if (redeem600Btn != null) {
            redeem600Btn.setOnClickListener(v -> attemptRedeem(600, 350));
        }
        if (redeem700Btn != null) {
            redeem700Btn.setOnClickListener(v -> attemptRedeem(700, 400));
        }
        if (redeem800Btn != null) {
            redeem800Btn.setOnClickListener(v -> attemptRedeem(800, 450));
        }
        if (redeem900Btn != null) {
            redeem900Btn.setOnClickListener(v -> attemptRedeem(900, 500));
        }
        if (redeem1000Btn != null) {
            redeem1000Btn.setOnClickListener(v -> attemptRedeem(1000, 700));
        }
        if (redeem1500Btn != null) {
            redeem1500Btn.setOnClickListener(v -> attemptRedeem(1500, 1100));
        }
        if (redeem2000Btn != null) {
            redeem2000Btn.setOnClickListener(v -> attemptRedeem(2000, 1500));
        }
        refreshRedemptionButtonsState();
    }

    private void refreshRedemptionButtonsState() {
        if (redeem100Btn != null) redeem100Btn.setEnabled(totalPointsValue >= 100);
        if (redeem200Btn != null) redeem200Btn.setEnabled(totalPointsValue >= 200);
        if (redeem300Btn != null) redeem300Btn.setEnabled(totalPointsValue >= 300);
        if (redeem400Btn != null) redeem400Btn.setEnabled(totalPointsValue >= 400);
        if (redeem500Btn != null) redeem500Btn.setEnabled(totalPointsValue >= 500);
        if (redeem600Btn != null) redeem600Btn.setEnabled(totalPointsValue >= 600);
        if (redeem700Btn != null) redeem700Btn.setEnabled(totalPointsValue >= 700);
        if (redeem800Btn != null) redeem800Btn.setEnabled(totalPointsValue >= 800);
        if (redeem900Btn != null) redeem900Btn.setEnabled(totalPointsValue >= 900);
        if (redeem1000Btn != null) redeem1000Btn.setEnabled(totalPointsValue >= 1000);
        if (redeem1500Btn != null) redeem1500Btn.setEnabled(totalPointsValue >= 1500);
        if (redeem2000Btn != null) redeem2000Btn.setEnabled(totalPointsValue >= 2000);
    }

    private void attemptRedeem(int requiredPoints, int reloadAmount) {
        if ((userPhone == null || userPhone.isEmpty()) && (userEmail == null || userEmail.isEmpty())) {
            android.widget.Toast.makeText(this, "Login with phone or email to redeem", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalPointsValue < requiredPoints) {
            android.widget.Toast.makeText(this, getString(R.string.insufficient_points), android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        totalPointsValue -= requiredPoints;
        if (userPhone != null && !userPhone.isEmpty()) {
            citizenDb.updateUserPointsByPhone(userPhone, totalPointsValue);
        } else {
            int streak = citizenDb.getUserStreak(userEmail);
            citizenDb.updateUserStats(userEmail, totalPointsValue, userLevel, streak, currentXpValue);
        }
        totalPoints.setText(String.valueOf(totalPointsValue));
        refreshRedemptionButtonsState();
        android.widget.Toast.makeText(this, "Redeemed Rs." + reloadAmount + " mobile reload", android.widget.Toast.LENGTH_SHORT).show();
    }

    private Level getCurrentLevel() {
        List<Level> levels = getLevels();
        for (Level level : levels) {
            if (level.getLevelNumber() == userLevel) {
                return level;
            }
        }
        return levels.get(0); // Fallback to first level
    }

    private void setupLevelProgressionRecyclerView() {
        List<Level> levels = getLevels();
        levelAdapter = new LevelAdapter(this, levels);
        levelProgressionRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        levelProgressionRecyclerView.setAdapter(levelAdapter);
        
        // Add professional animation to the level progression
        levelProgressionRecyclerView.setLayoutAnimation(
            android.view.animation.AnimationUtils.loadLayoutAnimation(this, R.anim.level_layout_animation));
        
        // Add item decoration for better spacing
        levelProgressionRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = 16;
                }
                if (position == levels.size() - 1) {
                    outRect.right = 16;
                }
            }
        });
    }

    private List<Level> getLevels() {
        List<Level> levels = new ArrayList<>();
        
        // Level 1 - Green Seed 🌱
        levels.add(new Level(1, getString(R.string.level_1_title), getString(R.string.level_1_description), "🌱", 0, true, userLevel == 1));
        
        // Level 2 - Eco Explorer 🧭
        levels.add(new Level(2, getString(R.string.level_2_title), getString(R.string.level_2_description), "🧭", 50, userLevel >= 2, userLevel == 2));
        
        // Level 3 - Recycle Rookie ♻️
        levels.add(new Level(3, getString(R.string.level_3_title), getString(R.string.level_3_description), "♻️", 150, userLevel >= 3, userLevel == 3));
        
        // Level 4 - Waste Warrior 🛡️
        levels.add(new Level(4, getString(R.string.level_4_title), getString(R.string.level_4_description), "🛡️", 300, userLevel >= 4, userLevel == 4));
        
        // Level 5 - Clean-Up Champion 🏆
        levels.add(new Level(5, getString(R.string.level_5_title), getString(R.string.level_5_description), "🏆", 500, userLevel >= 5, userLevel == 5));
        
        // Level 6 - Planet Protector 🌍
        levels.add(new Level(6, getString(R.string.level_6_title), getString(R.string.level_6_description), "🌍", 1000, userLevel >= 6, userLevel == 6));
        
        return levels;
    }

    private void animateProgress() {
        // Recompute progress to animate smoothly
        List<Level> levels = getLevels();
        Level currentLevel = getCurrentLevel();
        int currentThreshold = currentLevel.getRequiredPoints();
        int nextThreshold = currentThreshold;
        for (Level l : levels) {
            if (l.getLevelNumber() == currentLevel.getLevelNumber() + 1) {
                nextThreshold = l.getRequiredPoints();
                break;
            }
        }
        int totalNeeded = Math.max(1, nextThreshold - currentThreshold);
        int gainedInLevel = Math.max(0, currentXpValue - currentThreshold);
        int progress = Math.max(0, Math.min(100, (gainedInLevel * 100) / totalNeeded));
        ObjectAnimator animation = ObjectAnimator.ofInt(levelProgress, "progress", 0, progress);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    // removed rewards store for a simpler, focused Levels page

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
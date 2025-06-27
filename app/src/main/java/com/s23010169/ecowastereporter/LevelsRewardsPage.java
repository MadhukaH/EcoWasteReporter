package com.s23010169.ecowastereporter;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.LevelAdapter;
import com.s23010169.ecowastereporter.adapters.RewardAdapter;
import com.s23010169.ecowastereporter.models.Level;
import com.s23010169.ecowastereporter.models.Reward;
import java.util.ArrayList;
import java.util.List;

public class LevelsRewardsPage extends AppCompatActivity implements RewardAdapter.OnRewardActionListener {
    private TabLayout tabLayout;
    private View levelInfoCard;
    private RecyclerView rewardsRecyclerView;
    private RecyclerView achievementsRecyclerView;
    private RecyclerView levelProgressionRecyclerView;
    private TextView levelTitle, currentXp, xpToNext, totalPoints, streakCount;
    private LinearProgressIndicator levelProgress;
    private RewardAdapter rewardAdapter;
    private LevelAdapter levelAdapter;

    // Mock user data - In a real app, this would come from a database
    private final int userLevel = 3; // Current level (1-6)
    private final int currentXpValue = 2450;
    private final int xpToNextValue = 3000;
    private final int totalPointsValue = 15680;
    private final int streakValue = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_rewards);

        initializeViews();
        setupToolbar();
        setupTabLayout();
        loadUserData();
        setupLevelProgressionRecyclerView();
        setupRewardsRecyclerView();
        setupAchievementsRecyclerView();
        animateProgress();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        levelInfoCard = findViewById(R.id.levelInfoCard);
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView);
        achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);
        levelProgressionRecyclerView = findViewById(R.id.levelProgressionRecyclerView);
        levelTitle = findViewById(R.id.levelTitle);
        currentXp = findViewById(R.id.currentXp);
        xpToNext = findViewById(R.id.xpToNext);
        totalPoints = findViewById(R.id.totalPoints);
        streakCount = findViewById(R.id.streakCount);
        levelProgress = findViewById(R.id.levelProgress);
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

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateUI(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadUserData() {
        // Get current level info
        Level currentLevel = getCurrentLevel();
        levelTitle.setText(currentLevel.getTitle());
        currentXp.setText(getString(R.string.current_xp, currentXpValue));
        xpToNext.setText(getString(R.string.xp_needed, xpToNextValue - currentXpValue));
        totalPoints.setText(String.valueOf(totalPointsValue));
        streakCount.setText(String.valueOf(streakValue));

        // Set progress
        levelProgress.setMax(100);
        levelProgress.setProgress(0); // Start at 0 for animation
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
        
        // Add animation to the level progression
        levelProgressionRecyclerView.setLayoutAnimation(
            android.view.animation.AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down));
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
        int progress = (currentXpValue * 100) / xpToNextValue;
        ObjectAnimator animation = ObjectAnimator.ofInt(levelProgress, "progress", 0, progress);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setupRewardsRecyclerView() {
        List<Reward> rewards = getMockRewards();
        rewardAdapter = new RewardAdapter(this, rewards, totalPointsValue, this);
        rewardsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        rewardsRecyclerView.setAdapter(rewardAdapter);
    }

    private void setupAchievementsRecyclerView() {
        // TODO: Create Achievement model and adapter
        achievementsRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // achievementsRecyclerView.setAdapter(achievementAdapter);
    }

    private List<Reward> getMockRewards() {
        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward(1, "Eco Coffee Cup", 500, "item", "☕", false));
        rewards.add(new Reward(2, "Recycling Badge", 1000, "badge", "♻️", true));
        rewards.add(new Reward(3, "Tree Planting Day", 2000, "experience", "🌳", false));
        rewards.add(new Reward(4, "Eco Workshop", 5000, "education", "📚", false));
        rewards.add(new Reward(5, "Solar Charger", 8000, "item", "☀️", false));
        rewards.add(new Reward(6, "Beach Cleanup", 1500, "experience", "🏖️", false));
        rewards.add(new Reward(7, "Eco Water Bottle", 3000, "item", "💧", false));
        rewards.add(new Reward(8, "Green Energy Credits", 800, "credits", "⚡", false));
        return rewards;
    }

    private void updateUI(int tabPosition) {
        if (tabPosition == 0) { // Level Progress
            levelInfoCard.setVisibility(View.VISIBLE);
            levelProgressionRecyclerView.setVisibility(View.VISIBLE);
            achievementsRecyclerView.setVisibility(View.VISIBLE);
            rewardsRecyclerView.setVisibility(View.GONE);
        } else { // Rewards Store
            levelInfoCard.setVisibility(View.GONE);
            levelProgressionRecyclerView.setVisibility(View.GONE);
            achievementsRecyclerView.setVisibility(View.GONE);
            rewardsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClaimReward(Reward reward) {
        // In a real app, this would update the database
        Toast.makeText(this, "Claimed " + reward.getName() + "!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
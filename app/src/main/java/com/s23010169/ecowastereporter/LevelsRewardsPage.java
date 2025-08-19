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
import com.s23010169.ecowastereporter.models.CitizenDatabaseHelper;
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

    private CitizenDatabaseHelper citizenDb;
    private String userEmail;
    private int userLevel = 1;
    private int currentXpValue = 0;
    private int xpToNextValue = 100; // will adjust based on level thresholds
    private int totalPointsValue = 0;
    private int streakValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels_rewards);

        initializeViews();
        citizenDb = new CitizenDatabaseHelper(this);
        citizenDb.seedRewardsIfEmpty();
        userEmail = getIntent().getStringExtra("email");
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
        // Load from DB if we have a user
        if (userEmail != null) {
            userLevel = citizenDb.getUserLevel(userEmail);
            currentXpValue = citizenDb.getUserCurrentXp(userEmail);
            totalPointsValue = citizenDb.getUserPoints(userEmail);
            streakValue = citizenDb.getUserStreak(userEmail);
        }

        List<Level> levels = getLevels();
        Level currentLevel = getCurrentLevel();
        levelTitle.setText(currentLevel.getTitle());
        currentXp.setText(getString(R.string.current_xp, currentXpValue));

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
        int needed = Math.max(0, nextThreshold - currentXpValue);
        xpToNext.setText(getString(R.string.xp_needed, needed));
        totalPoints.setText(String.valueOf(totalPointsValue));
        streakCount.setText(String.valueOf(streakValue));

        // Set progress
        levelProgress.setMax(100);
        int progressPct = Math.max(0, Math.min(100, (gainedInLevel * 100) / totalNeeded));
        levelProgress.setProgress(0); // Start at 0 for animation
        // store in field for animateProgress to use
        this.xpToNextValue = nextThreshold; // keep for compat
        this.currentXpValue = currentXpValue; // unchanged
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

    private void setupRewardsRecyclerView() {
        List<Reward> rewards = citizenDb.getAllRewards();
        // mark claimed
        if (userEmail != null) {
            List<Integer> claimedIds = citizenDb.getClaimedRewardIdsForUser(userEmail);
            for (Reward r : rewards) {
                if (claimedIds.contains(r.getId())) {
                    r.setClaimed(true);
                }
            }
        }
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

    // removed mock rewards; now loaded from DB

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
        if (userEmail == null) {
            Toast.makeText(this, "Login required to claim rewards", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalPointsValue < reward.getPoints()) {
            Toast.makeText(this, getString(R.string.insufficient_points), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean ok = citizenDb.claimReward(userEmail, reward.getId());
        if (ok) {
            reward.setClaimed(true);
            rewardAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Claimed " + reward.getName() + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to claim reward", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
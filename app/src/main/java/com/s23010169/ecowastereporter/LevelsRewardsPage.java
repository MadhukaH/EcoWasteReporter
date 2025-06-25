package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.s23010169.ecowastereporter.adapters.RewardAdapter;
import com.s23010169.ecowastereporter.models.Reward;
import java.util.ArrayList;
import java.util.List;

public class LevelsRewardsPage extends AppCompatActivity implements RewardAdapter.OnRewardActionListener {
    private TabLayout tabLayout;
    private View levelInfoCard;
    private RecyclerView rewardsRecyclerView;
    private TextView levelTitle, currentXp, xpToNext, totalPoints, streakCount;
    private LinearProgressIndicator levelProgress;
    private RewardAdapter rewardAdapter;

    // Mock user data - In a real app, this would come from a database
    private final int userLevel = 12;
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
        setupRewardsRecyclerView();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        levelInfoCard = findViewById(R.id.levelInfoCard);
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView);
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
        levelTitle.setText(getString(R.string.level_format, userLevel));
        currentXp.setText(getString(R.string.current_xp, currentXpValue));
        xpToNext.setText(getString(R.string.xp_needed, xpToNextValue - currentXpValue));
        totalPoints.setText(String.valueOf(totalPointsValue));
        streakCount.setText(String.valueOf(streakValue));

        // Set progress
        int progress = (currentXpValue * 100) / xpToNextValue;
        levelProgress.setProgress(progress);
    }

    private void setupRewardsRecyclerView() {
        List<Reward> rewards = getMockRewards();
        rewardAdapter = new RewardAdapter(this, rewards, totalPointsValue, this);
        rewardsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        rewardsRecyclerView.setAdapter(rewardAdapter);
    }

    private List<Reward> getMockRewards() {
        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward(1, "Coffee Voucher", 500, "voucher", "☕", false));
        rewards.add(new Reward(2, "Premium Badge", 1000, "badge", "⭐", true));
        rewards.add(new Reward(3, "Team Lunch", 2000, "experience", "🍽️", false));
        rewards.add(new Reward(4, "Extra Day Off", 5000, "time", "🏖️", false));
        rewards.add(new Reward(5, "Tech Gadget", 8000, "item", "📱", false));
        rewards.add(new Reward(6, "Learning Course", 1500, "education", "📚", false));
        rewards.add(new Reward(7, "Gym Membership", 3000, "health", "💪", false));
        rewards.add(new Reward(8, "Movie Tickets", 800, "entertainment", "🎬", false));
        return rewards;
    }

    private void updateUI(int tabPosition) {
        if (tabPosition == 0) { // Level Progress
            levelInfoCard.setVisibility(View.VISIBLE);
            rewardsRecyclerView.setVisibility(View.GONE);
        } else { // Rewards Store
            levelInfoCard.setVisibility(View.GONE);
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
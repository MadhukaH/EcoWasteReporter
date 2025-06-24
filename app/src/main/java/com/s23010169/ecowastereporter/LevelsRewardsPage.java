package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.s23010169.ecowastereporter.adapters.RewardsViewPagerAdapter;
import com.s23010169.ecowastereporter.models.UserStats;

public class LevelsRewardsPage extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView userAvatar;
    private TextView userName;
    private TextView userLevel;
    private TextView totalPoints;
    private RewardsViewPagerAdapter viewPagerAdapter;
    private UserStats userStats;

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
        initializeViews();
        
        // Load user data
        loadUserData();

        // Setup ViewPager and TabLayout
        setupViewPager();
        setupTabLayout();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        userAvatar = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        userLevel = findViewById(R.id.userLevel);
        totalPoints = findViewById(R.id.totalPoints);
    }

    private void loadUserData() {
        // TODO: Load actual user data from database
        userStats = new UserStats(
            "Alex Johnson",  // name
            12,             // level
            2450,          // xp
            3000,          // xpToNext
            15680,         // totalPoints
            7,             // streak
            23,            // rank
            "🚀"           // avatar
        );

        // Update header UI
        userAvatar.setText(userStats.getAvatar());
        userName.setText(userStats.getName());
        userLevel.setText(String.format("Level %d • Rank #%d", userStats.getLevel(), userStats.getRank()));
        totalPoints.setText(String.valueOf(userStats.getTotalPoints()));
    }

    private void setupViewPager() {
        viewPagerAdapter = new RewardsViewPagerAdapter(this, userStats);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Profile");
                    break;
                case 1:
                    tab.setText("Rewards");
                    break;
                case 2:
                    tab.setText("Achievements");
                    break;
            }
        }).attach();
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
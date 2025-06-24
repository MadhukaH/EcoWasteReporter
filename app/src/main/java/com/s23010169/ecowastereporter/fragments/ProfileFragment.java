package com.s23010169.ecowastereporter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.UserStats;

public class ProfileFragment extends Fragment {
    private TextView xpProgress;
    private ProgressBar levelProgressBar;
    private TextView currentLevelText;
    private TextView xpToNextLevel;
    private TextView currentLevel;
    private TextView totalPointsValue;
    private TextView currentRank;
    private TextView streakDays;

    private UserStats userStats;

    public static ProfileFragment newInstance(UserStats userStats) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        // Add user stats to arguments if needed
        fragment.setArguments(args);
        fragment.userStats = userStats;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        updateUI();
    }

    private void initializeViews(View view) {
        xpProgress = view.findViewById(R.id.xpProgress);
        levelProgressBar = view.findViewById(R.id.levelProgressBar);
        currentLevelText = view.findViewById(R.id.currentLevelText);
        xpToNextLevel = view.findViewById(R.id.xpToNextLevel);
        currentLevel = view.findViewById(R.id.currentLevel);
        totalPointsValue = view.findViewById(R.id.totalPointsValue);
        currentRank = view.findViewById(R.id.currentRank);
        streakDays = view.findViewById(R.id.streakDays);
    }

    private void updateUI() {
        if (userStats == null) return;

        // Update XP progress
        xpProgress.setText(String.format("%d / %d XP", userStats.getXp(), userStats.getXpToNext()));
        levelProgressBar.setMax(userStats.getXpToNext());
        levelProgressBar.setProgress(userStats.getXp());

        // Update level info
        currentLevelText.setText(String.format("Level %d", userStats.getLevel()));
        xpToNextLevel.setText(String.format("%d XP to Level %d", 
            userStats.getXpToNext() - userStats.getXp(), 
            userStats.getLevel() + 1));

        // Update stats cards
        currentLevel.setText(String.valueOf(userStats.getLevel()));
        totalPointsValue.setText(String.valueOf(userStats.getTotalPoints()));
        currentRank.setText(String.format("#%d", userStats.getRank()));
        streakDays.setText(String.valueOf(userStats.getStreak()));
    }

    public void updateUserStats(UserStats userStats) {
        this.userStats = userStats;
        if (isAdded()) {
            updateUI();
        }
    }
} 
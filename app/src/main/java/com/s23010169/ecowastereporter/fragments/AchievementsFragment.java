package com.s23010169.ecowastereporter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.adapters.AchievementAdapter;
import com.s23010169.ecowastereporter.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;
    private List<Achievement> achievements = new ArrayList<>();

    public static AchievementsFragment newInstance() {
        return new AchievementsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        loadAchievements();
    }

    private void initializeViews(View view) {
        achievementsRecyclerView = view.findViewById(R.id.achievementsRecyclerView);
    }

    private void setupRecyclerView() {
        achievementAdapter = new AchievementAdapter(achievements);
        achievementsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        achievementsRecyclerView.setAdapter(achievementAdapter);
    }

    private void loadAchievements() {
        // Sample achievements data
        achievements.clear();
        achievements.add(new Achievement(
            "First Steps",
            "Complete your first task",
            true,
            "common"
        ));
        achievements.add(new Achievement(
            "Streak Master",
            "Maintain a 7-day streak",
            true,
            "rare"
        ));
        achievements.add(new Achievement(
            "Team Player",
            "Help 5 team members",
            false,
            "epic"
        ));
        achievements.add(new Achievement(
            "Innovation Hero",
            "Submit 3 improvement ideas",
            false,
            "legendary"
        ));
        achievementAdapter.notifyDataSetChanged();
    }

    public void updateAchievements(List<Achievement> newAchievements) {
        achievements.clear();
        achievements.addAll(newAchievements);
        if (achievementAdapter != null) {
            achievementAdapter.notifyDataSetChanged();
        }
    }
} 
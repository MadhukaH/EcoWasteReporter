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
import com.s23010169.ecowastereporter.adapters.RewardAdapter;
import com.s23010169.ecowastereporter.models.Reward;
import java.util.ArrayList;
import java.util.List;

public class RewardsFragment extends Fragment implements RewardAdapter.OnRewardClaimListener {
    private RecyclerView rewardsRecyclerView;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewards = new ArrayList<>();
    private int userPoints;

    public static RewardsFragment newInstance(int userPoints) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        args.putInt("userPoints", userPoints);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPoints = getArguments() != null ? getArguments().getInt("userPoints", 0) : 0;
        initializeViews(view);
        setupRecyclerView();
        loadRewards();
    }

    private void initializeViews(View view) {
        rewardsRecyclerView = view.findViewById(R.id.rewardsRecyclerView);
    }

    private void setupRecyclerView() {
        rewardAdapter = new RewardAdapter(rewards, userPoints, this);
        rewardsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rewardsRecyclerView.setAdapter(rewardAdapter);
    }

    private void loadRewards() {
        // Sample rewards data
        rewards.clear();
        rewards.add(new Reward(1, "Coffee Voucher", 500, "voucher", "☕", false));
        rewards.add(new Reward(2, "Premium Badge", 1000, "badge", "⭐", true));
        rewards.add(new Reward(3, "Team Lunch", 2000, "experience", "🍽️", false));
        rewards.add(new Reward(4, "Extra Day Off", 5000, "time", "🏖️", false));
        rewards.add(new Reward(5, "Tech Gadget", 8000, "item", "📱", false));
        rewards.add(new Reward(6, "Learning Course", 1500, "education", "📚", false));
        rewardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRewardClaim(Reward reward) {
        // Handle reward claim
        reward.setClaimed(true);
        rewardAdapter.notifyDataSetChanged();
        // TODO: Update database and user points
    }

    public void updateUserPoints(int points) {
        this.userPoints = points;
        if (rewardAdapter != null) {
            rewardAdapter = new RewardAdapter(rewards, userPoints, this);
            rewardsRecyclerView.setAdapter(rewardAdapter);
        }
    }
} 
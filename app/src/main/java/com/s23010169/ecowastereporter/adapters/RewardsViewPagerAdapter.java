package com.s23010169.ecowastereporter.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.s23010169.ecowastereporter.fragments.ProfileFragment;
import com.s23010169.ecowastereporter.fragments.RewardsFragment;
import com.s23010169.ecowastereporter.fragments.AchievementsFragment;
import com.s23010169.ecowastereporter.models.UserStats;

public class RewardsViewPagerAdapter extends FragmentStateAdapter {
    private UserStats userStats;
    private ProfileFragment profileFragment;
    private RewardsFragment rewardsFragment;
    private AchievementsFragment achievementsFragment;

    public RewardsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, UserStats userStats) {
        super(fragmentActivity);
        this.userStats = userStats;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (profileFragment == null) {
                    profileFragment = ProfileFragment.newInstance(userStats);
                }
                return profileFragment;
            case 1:
                if (rewardsFragment == null) {
                    rewardsFragment = RewardsFragment.newInstance(userStats.getTotalPoints());
                }
                return rewardsFragment;
            case 2:
                if (achievementsFragment == null) {
                    achievementsFragment = AchievementsFragment.newInstance();
                }
                return achievementsFragment;
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void updateUserStats(UserStats userStats) {
        this.userStats = userStats;
        if (profileFragment != null) {
            profileFragment.updateUserStats(userStats);
        }
        if (rewardsFragment != null) {
            rewardsFragment.updateUserPoints(userStats.getTotalPoints());
        }
    }
} 
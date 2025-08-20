package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Reward;
import java.util.List;

// This adapter displays available rewards that users can redeem with their points.
public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {
    private final Context context;
    private final List<Reward> rewards;
    private final int userPoints;
    private final OnRewardActionListener listener;

    public interface OnRewardActionListener {
        void onClaimReward(Reward reward);
    }

    public RewardAdapter(Context context, List<Reward> rewards, int userPoints, OnRewardActionListener listener) {
        this.context = context;
        this.rewards = rewards;
        this.userPoints = userPoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewards.get(position);
        holder.bind(reward);
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        private final TextView iconText;
        private final TextView nameText;
        private final TextView pointsText;
        private final Button claimButton;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            iconText = itemView.findViewById(R.id.rewardIcon);
            nameText = itemView.findViewById(R.id.rewardName);
            pointsText = itemView.findViewById(R.id.rewardPoints);
            claimButton = itemView.findViewById(R.id.claimButton);
        }

        void bind(Reward reward) {
            iconText.setText(reward.getIcon());
            nameText.setText(reward.getName());
            pointsText.setText(context.getString(R.string.points_format, reward.getPoints()));

            if (reward.isClaimed()) {
                claimButton.setText(R.string.claimed);
                claimButton.setEnabled(false);
                claimButton.setBackgroundTintList(context.getColorStateList(R.color.green_100));
                claimButton.setTextColor(context.getColor(R.color.green_700));
            } else if (userPoints >= reward.getPoints()) {
                claimButton.setText(R.string.claim_reward);
                claimButton.setEnabled(true);
                claimButton.setBackgroundTintList(context.getColorStateList(R.color.green_500));
                claimButton.setTextColor(context.getColor(android.R.color.white));
                claimButton.setOnClickListener(v -> listener.onClaimReward(reward));
            } else {
                claimButton.setText(R.string.insufficient_points);
                claimButton.setEnabled(false);
                claimButton.setBackgroundTintList(context.getColorStateList(R.color.gray_100));
                claimButton.setTextColor(context.getColor(R.color.gray_400));
            }
        }
    }
} 
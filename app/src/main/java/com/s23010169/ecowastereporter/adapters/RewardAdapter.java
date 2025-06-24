package com.s23010169.ecowastereporter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Reward;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {
    private List<Reward> rewards;
    private int userPoints;
    private OnRewardClaimListener listener;

    public interface OnRewardClaimListener {
        void onRewardClaim(Reward reward);
    }

    public RewardAdapter(List<Reward> rewards, int userPoints, OnRewardClaimListener listener) {
        this.rewards = rewards;
        this.userPoints = userPoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
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
        private TextView iconText;
        private TextView nameText;
        private TextView pointsText;
        private Button claimButton;
        private CardView cardView;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            iconText = itemView.findViewById(R.id.rewardIcon);
            nameText = itemView.findViewById(R.id.rewardName);
            pointsText = itemView.findViewById(R.id.rewardPoints);
            claimButton = itemView.findViewById(R.id.claimButton);
            cardView = itemView.findViewById(R.id.rewardCard);
        }

        void bind(final Reward reward) {
            iconText.setText(reward.getIcon());
            nameText.setText(reward.getName());
            pointsText.setText(String.format("%d points", reward.getPoints()));

            if (reward.isClaimed()) {
                claimButton.setText("Claimed ✓");
                claimButton.setEnabled(false);
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.green_50));
            } else if (userPoints >= reward.getPoints()) {
                claimButton.setText("Claim Reward");
                claimButton.setEnabled(true);
                claimButton.setOnClickListener(v -> listener.onRewardClaim(reward));
            } else {
                claimButton.setText("Insufficient Points");
                claimButton.setEnabled(false);
            }
        }
    }
} 
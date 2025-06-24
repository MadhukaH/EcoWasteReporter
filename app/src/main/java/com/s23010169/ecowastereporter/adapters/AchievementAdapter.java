package com.s23010169.ecowastereporter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Achievement;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
    private List<Achievement> achievements;

    public AchievementAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private ImageView medalIcon;
        private CardView cardView;

        AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.achievementName);
            descriptionText = itemView.findViewById(R.id.achievementDescription);
            medalIcon = itemView.findViewById(R.id.medalIcon);
            cardView = itemView.findViewById(R.id.achievementCard);
        }

        void bind(Achievement achievement) {
            nameText.setText(achievement.getName());
            descriptionText.setText(achievement.getDescription());

            int colorRes = achievement.getColorRes();
            if (achievement.isUnlocked()) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(colorRes));
                nameText.setTextColor(itemView.getContext().getColor(android.R.color.white));
                descriptionText.setTextColor(itemView.getContext().getColor(android.R.color.white));
                medalIcon.setImageResource(R.drawable.ic_mark_complete);
                medalIcon.setAlpha(1.0f);
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(android.R.color.white));
                nameText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                descriptionText.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                medalIcon.setImageResource(R.drawable.ic_mark_complete);
                medalIcon.setAlpha(0.5f);
            }
        }
    }
} 
package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Level;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
    private List<Level> levels;
    private Context context;

    public LevelAdapter(Context context, List<Level> levels) {
        this.context = context;
        this.levels = levels;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        Level level = levels.get(position);
        holder.bind(level, position);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    class LevelViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView levelCard;
        TextView levelIcon;
        TextView levelTitle;
        TextView levelDescription;
        TextView levelPoints;
        View progressLine;

        LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelCard = itemView.findViewById(R.id.levelCard);
            levelIcon = itemView.findViewById(R.id.levelIcon);
            levelTitle = itemView.findViewById(R.id.levelTitle);
            levelDescription = itemView.findViewById(R.id.levelDescription);
            levelPoints = itemView.findViewById(R.id.levelPoints);
            progressLine = itemView.findViewById(R.id.progressLine);
        }

        void bind(Level level, int position) {
            levelIcon.setText(level.getIcon());
            levelTitle.setText(level.getTitle());
            levelDescription.setText(level.getDescription());
            levelPoints.setText(String.valueOf(level.getRequiredPoints()));

            // Set colors and backgrounds based on level status
            if (level.isCurrentLevel()) {
                // Current level - green theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.eco_gradient_background);
                levelIcon.setTextColor(context.getResources().getColor(R.color.eco_current_level_text));
                levelTitle.setTextColor(context.getResources().getColor(R.color.eco_current_level_text));
                levelDescription.setTextColor(context.getResources().getColor(R.color.green_600));
                levelPoints.setTextColor(context.getResources().getColor(R.color.eco_current_level_text));
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_current_level_border));
                levelCard.setStrokeWidth(4);
            } else if (level.isUnlocked()) {
                // Unlocked level - blue theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.eco_blue_gradient_background);
                levelIcon.setTextColor(context.getResources().getColor(R.color.eco_unlocked_level_text));
                levelTitle.setTextColor(context.getResources().getColor(R.color.eco_unlocked_level_text));
                levelDescription.setTextColor(context.getResources().getColor(R.color.blue_500));
                levelPoints.setTextColor(context.getResources().getColor(R.color.eco_unlocked_level_text));
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_unlocked_level_border));
                levelCard.setStrokeWidth(2);
            } else {
                // Locked level - gray theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.eco_gray_background);
                levelIcon.setTextColor(context.getResources().getColor(R.color.eco_locked_level_text));
                levelTitle.setTextColor(context.getResources().getColor(R.color.gray_600));
                levelDescription.setTextColor(context.getResources().getColor(R.color.eco_locked_level_text));
                levelPoints.setTextColor(context.getResources().getColor(R.color.gray_600));
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_locked_level_border));
                levelCard.setStrokeWidth(1);
            }

            // Show progress line between levels (except for the last one)
            if (position < levels.size() - 1) {
                progressLine.setVisibility(View.VISIBLE);
                if (level.isUnlocked() && levels.get(position + 1).isUnlocked()) {
                    progressLine.setBackgroundResource(R.drawable.eco_progress_line);
                } else if (level.isUnlocked()) {
                    progressLine.setBackgroundResource(R.drawable.eco_blue_progress_line);
                } else {
                    progressLine.setBackgroundColor(context.getResources().getColor(R.color.eco_progress_line_gray));
                }
            } else {
                progressLine.setVisibility(View.GONE);
            }
        }
    }
} 
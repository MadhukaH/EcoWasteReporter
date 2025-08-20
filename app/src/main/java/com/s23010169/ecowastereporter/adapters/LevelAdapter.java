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

// This adapter displays user levels with progress indicators and unlock status for the gamification system.
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
        TextView levelNumber;
        View progressLine;
        TextView progressArrow;

        LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelCard = itemView.findViewById(R.id.levelCard);
            levelIcon = itemView.findViewById(R.id.levelIcon);
            levelTitle = itemView.findViewById(R.id.levelTitle);
            levelDescription = itemView.findViewById(R.id.levelDescription);
            levelPoints = itemView.findViewById(R.id.levelPoints);
            levelNumber = itemView.findViewById(R.id.levelNumber);
            progressLine = itemView.findViewById(R.id.progressLine);
            progressArrow = itemView.findViewById(R.id.progressArrow);
        }

        void bind(Level level, int position) {
            // Set basic content
            levelIcon.setText(level.getIcon());
            levelTitle.setText(level.getTitle());
            levelDescription.setText(level.getDescription());
            levelPoints.setText(String.valueOf(level.getRequiredPoints()));
            levelNumber.setText(String.valueOf(level.getLevelNumber()));

            // Set colors and backgrounds based on level status
            if (level.isCurrentLevel()) {
                // Current level - Premium green theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.level_current_gradient);
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_current_level_border));
                levelCard.setStrokeWidth(3);
                levelCard.setCardElevation(12f);
                
                levelIcon.setTextColor(Color.WHITE);
                levelTitle.setTextColor(context.getResources().getColor(R.color.eco_current_level_text));
                levelDescription.setTextColor(context.getResources().getColor(R.color.green_600));
                levelPoints.setTextColor(Color.WHITE);
                levelNumber.setTextColor(Color.WHITE);
                levelNumber.setBackgroundResource(R.drawable.level_number_badge);
                
            } else if (level.isUnlocked()) {
                // Unlocked level - Premium blue theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.level_unlocked_gradient);
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_unlocked_level_border));
                levelCard.setStrokeWidth(2);
                levelCard.setCardElevation(8f);
                
                levelIcon.setTextColor(Color.WHITE);
                levelTitle.setTextColor(context.getResources().getColor(R.color.eco_unlocked_level_text));
                levelDescription.setTextColor(context.getResources().getColor(R.color.blue_500));
                levelPoints.setTextColor(Color.WHITE);
                levelNumber.setTextColor(Color.WHITE);
                levelNumber.setBackgroundResource(R.drawable.level_number_badge);
                
            } else {
                // Locked level - Premium gray theme
                levelCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                levelCard.setBackgroundResource(R.drawable.level_locked_gradient);
                levelCard.setStrokeColor(context.getResources().getColor(R.color.eco_locked_level_border));
                levelCard.setStrokeWidth(1);
                levelCard.setCardElevation(4f);
                
                levelIcon.setTextColor(context.getResources().getColor(R.color.gray_400));
                levelTitle.setTextColor(context.getResources().getColor(R.color.gray_600));
                levelDescription.setTextColor(context.getResources().getColor(R.color.eco_locked_level_text));
                levelPoints.setTextColor(context.getResources().getColor(R.color.gray_500));
                levelNumber.setTextColor(context.getResources().getColor(R.color.gray_400));
                levelNumber.setBackgroundColor(context.getResources().getColor(R.color.gray_300));
            }

            // Show progress line between levels (except for the last one)
            if (position < levels.size() - 1) {
                progressLine.setVisibility(View.VISIBLE);
                progressArrow.setVisibility(View.VISIBLE);
                
                if (level.isUnlocked() && levels.get(position + 1).isUnlocked()) {
                    progressLine.setBackgroundResource(R.drawable.progress_line_active);
                    progressArrow.setTextColor(context.getResources().getColor(R.color.eco_progress_line_green));
                } else if (level.isUnlocked()) {
                    progressLine.setBackgroundResource(R.drawable.progress_line_unlocked);
                    progressArrow.setTextColor(context.getResources().getColor(R.color.eco_progress_line_blue));
                } else {
                    progressLine.setBackgroundColor(context.getResources().getColor(R.color.eco_progress_line_gray));
                    progressArrow.setTextColor(context.getResources().getColor(R.color.eco_progress_line_gray));
                }
            } else {
                progressLine.setVisibility(View.GONE);
                progressArrow.setVisibility(View.GONE);
            }
        }
    }
} 
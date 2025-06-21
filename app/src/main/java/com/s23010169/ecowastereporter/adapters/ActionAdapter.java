package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Action;

import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {
    private final List<Action> actions;
    private final Context context;
    private final ActionClickListener listener;
    private int lastPosition = -1;

    public interface ActionClickListener {
        void onActionClick(Action action);
    }

    public ActionAdapter(Context context, List<Action> actions, ActionClickListener listener) {
        this.context = context;
        this.actions = actions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_action, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
        Action action = actions.get(position);
        holder.bind(action);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    static class ActionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView iconView;
        private final TextView titleView;

        ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.actionCard);
            iconView = itemView.findViewById(R.id.actionIcon);
            titleView = itemView.findViewById(R.id.actionTitle);
        }

        void bind(Action action) {
            iconView.setImageResource(action.getIconResId());
            titleView.setText(action.getTitle());
            cardView.setOnClickListener(v -> action.getOnClick().run());
        }
    }
} 
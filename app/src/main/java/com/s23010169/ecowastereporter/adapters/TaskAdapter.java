package com.s23010169.ecowastereporter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onGetRouteClick(Task task);
        void onMarkDoneClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView locationTextView;
        private TextView priorityTextView;
        private TextView binDetailsTextView;
        private TextView distanceTextView;
        private Button getRouteButton;
        private Button markDoneButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            binDetailsTextView = itemView.findViewById(R.id.binDetailsTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            getRouteButton = itemView.findViewById(R.id.getRouteButton);
            markDoneButton = itemView.findViewById(R.id.markDoneButton);
        }

        public void bind(Task task) {
            locationTextView.setText(task.getLocation());
            priorityTextView.setText(task.getPriority());
            binDetailsTextView.setText(task.getBinDetails());
            distanceTextView.setText(task.getFormattedDistance() + " • " + task.getFormattedTime());

            // Set background for priority tag
            switch (task.getPriority().toUpperCase()) {
                case "HIGH":
                    priorityTextView.setBackgroundResource(R.drawable.status_background_pending);
                    break;
                case "MED":
                    priorityTextView.setBackgroundResource(R.drawable.status_background_in_progress);
                    break;
                case "LOW":
                    priorityTextView.setBackgroundResource(R.drawable.status_background_resolved);
                    break;
            }

            getRouteButton.setOnClickListener(v -> listener.onGetRouteClick(task));
            markDoneButton.setOnClickListener(v -> listener.onMarkDoneClick(task));
        }
    }
} 
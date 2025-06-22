package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Task;

import java.util.List;

public class TaskSelectorAdapter extends RecyclerView.Adapter<TaskSelectorAdapter.TaskViewHolder> {
    private final Context context;
    private final List<Task> tasks;
    private final TaskSelectionListener listener;
    private int selectedPosition = 0;

    public interface TaskSelectionListener {
        void onTaskSelected(Task task, int position);
    }

    public TaskSelectorAdapter(Context context, List<Task> tasks, TaskSelectionListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_select, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskId.setText(task.getTaskId());
        holder.taskLocation.setText(task.getLocation());
        holder.taskDescription.setText(task.getDescription());
        holder.taskStatus.setText(task.getStatus());
        holder.taskDistance.setText(task.getDistance());

        // Set status background and text color based on status
        int statusBackground;
        int statusTextColor;
        switch (task.getStatus().toLowerCase()) {
            case "in progress":
                statusBackground = R.drawable.status_background_in_progress;
                statusTextColor = context.getColor(R.color.status_in_progress);
                break;
            case "pending":
                statusBackground = R.drawable.status_background_pending;
                statusTextColor = context.getColor(R.color.status_pending);
                break;
            default:
                statusBackground = R.drawable.status_background;
                statusTextColor = context.getColor(R.color.text_secondary);
                break;
        }
        holder.taskStatus.setBackgroundResource(statusBackground);
        holder.taskStatus.setTextColor(statusTextColor);

        // Handle item selection
        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != position) {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                listener.onTaskSelected(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskId, taskLocation, taskDescription, taskStatus, taskDistance;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskId = itemView.findViewById(R.id.taskId);
            taskLocation = itemView.findViewById(R.id.taskLocation);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskDistance = itemView.findViewById(R.id.taskDistance);
        }
    }
} 
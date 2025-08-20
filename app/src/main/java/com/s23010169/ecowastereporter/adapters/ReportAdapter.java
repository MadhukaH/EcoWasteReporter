package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.ReportWastePage;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import java.util.List;

// This adapter displays waste reports in a list with edit, delete, and photo preview functionality.
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<Report> reports;
    private final Context context;
    private final ReportDatabaseHelper dbHelper;

    public ReportAdapter(Context context, List<Report> reports) {
        this.context = context;
        this.reports = reports;
        this.dbHelper = new ReportDatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reports.get(position);

        // Set report ID
        holder.reportIdText.setText("Report #" + report.getReportId());

        // Set waste type
        holder.wasteTypeText.setText(report.getWasteType());

        // Set location
        holder.locationText.setText(report.getLocation());

        // Set description
        holder.descriptionText.setText(report.getDescription());

        // Set status with appropriate background
        holder.statusText.setText(report.getStatus());
        int statusColor;
        switch (report.getStatus().toLowerCase()) {
            case "resolved":
                statusColor = R.drawable.status_background_resolved;
                break;
            case "in progress":
                statusColor = R.drawable.status_background_in_progress;
                break;
            default:
                statusColor = R.drawable.status_background_pending;
                break;
        }
        holder.statusText.setBackgroundResource(statusColor);

        // Set timestamp
        long now = System.currentTimeMillis();
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                report.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);
        holder.timestampText.setText(timeAgo);

        // Setup photos RecyclerView if there are photos
        if (report.getPhotoUris() != null && !report.getPhotoUris().isEmpty()) {
            holder.photosRecyclerView.setVisibility(View.VISIBLE);
            holder.photosRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            PhotoPreviewAdapter photoAdapter = new PhotoPreviewAdapter(report.getPhotoUris(), null);
            holder.photosRecyclerView.setAdapter(photoAdapter);
        } else {
            holder.photosRecyclerView.setVisibility(View.GONE);
        }

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportWastePage.class);
            intent.putExtra("REPORT_ID", report.getReportId());
            intent.putExtra("IS_EDITING", true);
            context.startActivity(intent);
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteReport(report.getReportId());
                    reports.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, reports.size());
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateReports(List<Report> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportIdText;
        TextView wasteTypeText;
        TextView locationText;
        TextView descriptionText;
        TextView statusText;
        TextView timestampText;
        RecyclerView photosRecyclerView;
        MaterialButton editButton;
        MaterialButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            reportIdText = itemView.findViewById(R.id.reportIdText);
            wasteTypeText = itemView.findViewById(R.id.wasteTypeText);
            locationText = itemView.findViewById(R.id.locationText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            statusText = itemView.findViewById(R.id.statusText);
            timestampText = itemView.findViewById(R.id.timestampText);
            photosRecyclerView = itemView.findViewById(R.id.photosRecyclerView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 
package com.s23010169.ecowastereporter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import com.s23010169.ecowastereporter.models.Report;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> reports;
    private Context context;

    public ReportAdapter(Context context, List<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.titleText.setText(report.getTitle());
        holder.locationText.setText(report.getLocation());
        holder.reportIdText.setText("Report ID: " + report.getReportId());
        holder.timeText.setText(report.getTimeAgo());
        holder.statusText.setText(report.getStatus());

        // Set status background color based on status
        int backgroundColor;
        switch (report.getStatus().toLowerCase()) {
            case "pending":
                backgroundColor = ContextCompat.getColor(context, R.color.status_pending);
                break;
            case "resolved":
                backgroundColor = ContextCompat.getColor(context, R.color.status_resolved);
                break;
            case "in progress":
                backgroundColor = ContextCompat.getColor(context, R.color.status_in_progress);
                break;
            default:
                backgroundColor = ContextCompat.getColor(context, R.color.green_500);
                break;
        }
        holder.statusText.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public void updateReports(List<Report> newReports) {
        this.reports = newReports;
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView locationText;
        TextView reportIdText;
        TextView timeText;
        TextView statusText;

        ReportViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            locationText = itemView.findViewById(R.id.locationText);
            reportIdText = itemView.findViewById(R.id.reportIdText);
            timeText = itemView.findViewById(R.id.timeText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
} 
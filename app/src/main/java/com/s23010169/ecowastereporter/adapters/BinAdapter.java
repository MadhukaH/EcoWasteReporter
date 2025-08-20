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
import com.s23010169.ecowastereporter.models.Bin;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.List;

// This adapter displays waste bins in a list or grid with location, fill level, and distance information.
public class BinAdapter extends RecyclerView.Adapter<BinAdapter.BinViewHolder> {
    private List<Bin> bins;
    private Context context;
    private OnBinClickListener listener;

    public interface OnBinClickListener {
        void onBinClick(Bin bin);
    }

    public BinAdapter(Context context, List<Bin> bins, OnBinClickListener listener) {
        this.context = context;
        this.bins = bins;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bin, parent, false);
        return new BinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BinViewHolder holder, int position) {
        Bin bin = bins.get(position);
        holder.bind(bin);
    }

    @Override
    public int getItemCount() {
        return bins.size();
    }

    public void updateBins(List<Bin> newBins) {
        this.bins = newBins;
        notifyDataSetChanged();
    }

    class BinViewHolder extends RecyclerView.ViewHolder {
        private CircularProgressIndicator progressFill;
        private TextView textFillPercent;
        private TextView textStatus;
        private TextView textBinName;
        private TextView textBinLevel;

        BinViewHolder(@NonNull View itemView) {
            super(itemView);
            progressFill = itemView.findViewById(R.id.progressFill);
            textFillPercent = itemView.findViewById(R.id.textFillPercent);
            textStatus = itemView.findViewById(R.id.textStatus);
            textBinName = itemView.findViewById(R.id.textBinName);
            textBinLevel = itemView.findViewById(R.id.textBinLevel);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBinClick(bins.get(position));
                }
            });
        }

        void bind(Bin bin) {
            int fill = bin.getFillPercentage();
            textFillPercent.setText(fill + "%");
            progressFill.setProgress(fill);
            textBinName.setText(bin.getLocation());
            textBinLevel.setText("Bin Level: " + fill + "%");
            // Status logic
            if (fill >= 90) {
                textStatus.setText("Requested");
                textStatus.setBackgroundResource(R.drawable.status_requested_bg);
                textStatus.setTextColor(ContextCompat.getColor(context, R.color.status_requested));
            } else if (fill == 0) {
                textStatus.setText("Pending");
                textStatus.setBackgroundResource(R.drawable.status_pending_bg);
                textStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
            } else {
                textStatus.setText("Active");
                textStatus.setBackgroundResource(R.drawable.status_active_bg);
                textStatus.setTextColor(ContextCompat.getColor(context, R.color.status_active));
            }
            // Set progress color based on fill percentage
            if (fill < 40) {
                progressFill.setIndicatorColor(ContextCompat.getColor(context, R.color.success_green));
            } else if (fill < 80) {
                progressFill.setIndicatorColor(ContextCompat.getColor(context, R.color.warning_yellow));
            } else {
                progressFill.setIndicatorColor(ContextCompat.getColor(context, R.color.error_red));
            }
        }
    }
} 
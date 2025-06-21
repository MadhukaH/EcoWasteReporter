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
import java.util.List;

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
        private View statusIndicator;
        private TextView locationText;
        private TextView statusText;

        BinViewHolder(@NonNull View itemView) {
            super(itemView);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            locationText = itemView.findViewById(R.id.locationText);
            statusText = itemView.findViewById(R.id.statusText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBinClick(bins.get(position));
                }
            });
        }

        void bind(Bin bin) {
            locationText.setText(bin.getLocation());
            String statusString = String.format("%d%% Full • %.1f km away", bin.getFillPercentage(), bin.getDistance());
            statusText.setText(statusString);

            int colorResId;
            if (bin.getFillPercentage() < 40) {
                colorResId = R.color.green;
            } else if (bin.getFillPercentage() < 80) {
                colorResId = R.color.yellow;
            } else {
                colorResId = R.color.red;
            }
            statusIndicator.setBackgroundTintList(ContextCompat.getColorStateList(context, colorResId));
        }
    }
} 
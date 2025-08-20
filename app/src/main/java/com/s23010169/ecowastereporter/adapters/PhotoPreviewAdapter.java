package com.s23010169.ecowastereporter.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.s23010169.ecowastereporter.R;
import java.util.List;

// This adapter displays photo previews in a horizontal list with delete functionality for waste reports.
public class PhotoPreviewAdapter extends RecyclerView.Adapter<PhotoPreviewAdapter.PhotoViewHolder> {
    private List<Uri> photos;
    private OnPhotoDeleteListener deleteListener;
    private OnPhotoClickListener clickListener;

    public interface OnPhotoDeleteListener {
        void onPhotoDelete(int position);
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(int position, Uri uri);
    }

    public PhotoPreviewAdapter(List<Uri> photos, OnPhotoDeleteListener deleteListener) {
        this.photos = photos;
        this.deleteListener = deleteListener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_preview, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri photoUri = photos.get(position);
        holder.photoPreview.setImageURI(photoUri);
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPhotoDelete(position);
            }
        });
        holder.photoPreview.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPhotoClick(position, photoUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void updatePhotos(List<Uri> newPhotos) {
        this.photos = newPhotos;
        notifyDataSetChanged();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoPreview;
        ImageButton deleteButton;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoPreview = itemView.findViewById(R.id.photoPreview);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 
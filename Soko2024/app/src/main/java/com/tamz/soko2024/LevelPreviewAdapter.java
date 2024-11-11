package com.tamz.soko2024;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LevelPreviewAdapter extends RecyclerView.Adapter<LevelPreviewAdapter.LevelViewHolder> {
    private final List<Bitmap> levelPreviews;
    private final List<String> levelNames; // Added for level names
    private final OnLevelClickListener onLevelClickListener;

    // Přidání listeneru pro kliknutí
    public LevelPreviewAdapter(List<Bitmap> levelPreviews, List<String> levelNames, OnLevelClickListener listener) {
        this.levelPreviews = levelPreviews;
        this.levelNames = levelNames;
        this.onLevelClickListener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_preview_item, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        holder.levelPreviewImage.setImageBitmap(levelPreviews.get(position));
        holder.levelName.setText(levelNames.get(position)); // Set level name

        // Přidání listeneru pro kliknutí na položku
        holder.itemView.setOnClickListener(v -> onLevelClickListener.onLevelClick(position));
    }

    @Override
    public int getItemCount() {
        return levelPreviews.size();
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        ImageView levelPreviewImage;
        TextView levelName;
        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            levelPreviewImage = itemView.findViewById(R.id.level_preview_image);
            levelName = itemView.findViewById(R.id.level_name);
        }
    }

    // Definice rozhraní pro kliknutí na level
    public interface OnLevelClickListener {
        void onLevelClick(int position);
    }
}

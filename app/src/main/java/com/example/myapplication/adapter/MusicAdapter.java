package com.example.myapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.RvEventItemBinding;
import com.example.myapplication.databinding.RvMusicItemBinding;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.model.MusicSubModel;
import com.example.myapplication.util.NetworkUtils;


import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private final List<MusicSubModel> musicSubModels;
    private final Activity activity;
    private final OnClick onClick;

    public MusicAdapter(List<MusicSubModel> musicSubModels, Activity activity,OnClick onClick) {
        this.musicSubModels = musicSubModels;
        this.activity = activity;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        RvMusicItemBinding binding = RvMusicItemBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, int position) {
        holder.binding.itemMusicTitleText.setText(musicSubModels.get(position).getmName());
        onClick.saveClick(holder.binding.itemMusicDownloadBtn, holder.binding.itemMusicPlayBtn, position);
        holder.binding.itemMusicDownloadBtn.setOnClickListener(v ->{
            NetworkUtils.setCustomIntent(activity, musicSubModels.get(position).getFile());
        });
        holder.binding.itemMusicDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onDownload(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicSubModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final RvMusicItemBinding binding;
        public ViewHolder(@NonNull RvMusicItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
    public interface OnClick {
        void onDownload(int position);

        void saveClick(ImageView itemMusicDownloadBtn, ImageView itemMusicPlayBtn, int position);
    }
}

package com.example.myapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.RvEventItemBinding;
import com.example.myapplication.model.EventSubModel;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<EventSubModel> eventSubModels;
    private final Activity activity;
    OnClickListener clickListener;

    public EventAdapter(List<EventSubModel> eventSubModels, Activity activity, OnClickListener clickListener) {
        this.eventSubModels = eventSubModels;
        this.activity = activity;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        RvEventItemBinding binding = RvEventItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.binding.itemEventTitleText.setText(eventSubModels.get(position).getTitle());

        Glide.with(holder.binding.itemEventImage)
                .load("https://event.sevenstepsschool.org" + eventSubModels.get(position).getFile())
                .into(holder.binding.itemEventImage);


        holder.itemView.setOnClickListener(view -> {
            clickListener.onclick(position);
        });

    }

    public interface OnClickListener {
        void onclick(int position);
    }

    @Override
    public int getItemCount() {
        return eventSubModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final RvEventItemBinding binding;

        public ViewHolder(@NonNull RvEventItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

package com.example.myapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.RvBookItemBinding;
import com.example.myapplication.model.BookSubModel;
import com.example.myapplication.util.NetworkUtils;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final List<BookSubModel> bookSubModels;
    private final Activity activity;
    OnClickListener onClickListener;

    public BookAdapter(List<BookSubModel> bookSubModels, Activity activity, OnClickListener onClickListener) {
        this.bookSubModels = bookSubModels;
        this.activity = activity;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        RvBookItemBinding binding = RvBookItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
//        Glide.with(holder.binding.itemBookImage)
//                .load("https://event.sevenstepsschool.org" + bookSubModels.get(position).getFile())
//                .into(holder.binding.itemBookImage);
        holder.binding.itemBookMainCard.setOnClickListener(view -> {
            NetworkUtils.setCustomIntent(activity, bookSubModels.get(position).getFile());
        });

        holder.binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onDownload(position);
            }
        });
    }

    public interface OnClickListener {
        void onDownload(int position);
    }

    @Override
    public int getItemCount() {
        return bookSubModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final RvBookItemBinding binding;

        public ViewHolder(@NonNull RvBookItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

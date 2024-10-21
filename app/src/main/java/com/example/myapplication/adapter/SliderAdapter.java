package com.example.myapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.SliderLayoutBinding;
import com.example.myapplication.model.SliderSubImage;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    private final List<SliderSubImage> mSliderItems;
    private final Activity activity;

    public SliderAdapter(List<SliderSubImage> mSliderItems, Activity activity) {
        this.mSliderItems = mSliderItems;
        this.activity = activity;
    }

    @Override
    public SliderAdapter.SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        SliderLayoutBinding binding = SliderLayoutBinding.inflate(inflater,parent,false);
        return new SliderAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SliderAdapter.SliderAdapterViewHolder viewHolder, int position) {
        Glide.with(viewHolder.itemView)
                .load(mSliderItems.get(position).getFile())
                .fitCenter()
                .into(viewHolder.binding.sliderImage);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public static class SliderAdapterViewHolder extends ViewHolder {
        final SliderLayoutBinding binding;
        public SliderAdapterViewHolder(SliderLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

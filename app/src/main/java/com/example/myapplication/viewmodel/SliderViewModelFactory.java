package com.example.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.myapplication.repository.SliderImgRepository;

public class SliderViewModelFactory implements ViewModelProvider.Factory {
    private final SliderImgRepository repository;

    public SliderViewModelFactory(SliderImgRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        return (T) new SliderViewModel(repository);
    }
}

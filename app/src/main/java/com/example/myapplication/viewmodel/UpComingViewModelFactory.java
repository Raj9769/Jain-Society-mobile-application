package com.example.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.myapplication.repository.EventRepository;
import com.example.myapplication.repository.UpcomingRepository;

public class UpComingViewModelFactory implements ViewModelProvider.Factory {
    private final UpcomingRepository repository;

    public UpComingViewModelFactory(UpcomingRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        return (T) new UpComingViewModel(repository);
    }
}

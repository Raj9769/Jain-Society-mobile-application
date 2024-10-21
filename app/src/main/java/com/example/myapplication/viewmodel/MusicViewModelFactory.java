package com.example.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.myapplication.repository.MusicRepository;

public class MusicViewModelFactory implements ViewModelProvider.Factory {
    private final MusicRepository repository;

    public MusicViewModelFactory(MusicRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        return (T) new MusicViewModel(repository);
    }
}

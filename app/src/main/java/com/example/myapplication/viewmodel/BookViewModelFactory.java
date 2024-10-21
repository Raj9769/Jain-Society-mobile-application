package com.example.myapplication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.myapplication.repository.BookRepository;
import com.example.myapplication.repository.EventRepository;

public class BookViewModelFactory implements ViewModelProvider.Factory {
    private final BookRepository repository;

    public BookViewModelFactory(BookRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        return (T) new BookViewModel(repository);
    }
}

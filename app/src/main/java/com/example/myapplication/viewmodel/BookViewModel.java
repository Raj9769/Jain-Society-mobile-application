package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.BookSubModel;
import com.example.myapplication.repository.BookRepository;
import com.example.myapplication.repository.Responce;

import java.util.List;

public class BookViewModel extends ViewModel {
    private final BookRepository repository;

    public BookViewModel(BookRepository repository) {
        this.repository = repository;
    }

    public LiveData<Responce<List<BookSubModel>>> getBookLiveData(){
        repository.getBookData();
        return repository.getBookLiveData();
    }

}

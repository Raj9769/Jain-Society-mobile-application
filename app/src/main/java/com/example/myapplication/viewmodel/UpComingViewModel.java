package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.BookSubModel;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.repository.BookRepository;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.repository.UpcomingRepository;

import java.util.List;

public class UpComingViewModel extends ViewModel {
    private final UpcomingRepository repository;

    public UpComingViewModel(UpcomingRepository repository) {
        this.repository = repository;
    }

    public LiveData<Responce<List<EventSubModel>>> getUpComingLiveData(){
        repository.getUpComingData();
        return repository.getUpComingLiveData();
    }

}

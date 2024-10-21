package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.repository.EventRepository;
import com.example.myapplication.repository.Responce;

import java.util.List;

public class EventViewModel extends ViewModel {
    private final EventRepository repository;

    public EventViewModel(EventRepository repository) {
        this.repository = repository;
    }

    public LiveData<Responce<List<EventSubModel>>> getEventLiveData(){
        repository.getEventData();
        return repository.getEventLiveData();
    }

}

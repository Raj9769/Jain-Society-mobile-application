package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.MusicSubModel;
import com.example.myapplication.repository.MusicRepository;
import com.example.myapplication.repository.Responce;

import java.util.List;

public class MusicViewModel extends ViewModel {
    private final MusicRepository repository;

    public MusicViewModel(MusicRepository repository) {
        this.repository = repository;
    }

    public LiveData<Responce<List<MusicSubModel>>> getMusicLiveData(){
        repository.getMusicData();
        return repository.getMusicLiveData();
    }

}

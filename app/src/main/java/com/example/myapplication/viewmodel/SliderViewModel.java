package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.SliderSubImage;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.repository.SliderImgRepository;

import java.util.List;

public class SliderViewModel extends ViewModel {
    private final SliderImgRepository repository;

    public SliderViewModel(SliderImgRepository repository) {
        this.repository = repository;
    }

    public LiveData<Responce<List<SliderSubImage>>> getSliderLiveData(){
        repository.getSliderData();
        return repository.getSliderLiveData();
    }

}

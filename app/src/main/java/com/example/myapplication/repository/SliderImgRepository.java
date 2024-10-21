package com.example.myapplication.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.SliderImage;
import com.example.myapplication.model.SliderSubImage;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SliderImgRepository {
    private static final String SliderImgTAG = "Book_TAG";

    private final Context context;
    private final MutableLiveData<Responce<List<SliderSubImage>>> sliderLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SliderImgRepository(Context context) {
        this.context = context;
    }

    public LiveData<Responce<List<SliderSubImage>>> getSliderLiveData() {
        return sliderLiveData;
    }

    public void getSliderData() {
        if (NetworkUtils.isNetworkConnected(context)) {
            sliderLiveData.postValue(Responce.loading(null));
            Observable<SliderImage> observable = ApiService.getClient(StaticData.API_BASE_URL)
                    .getImageData();

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SliderImage>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull SliderImage sliderImage) {
                            if (sliderImage.getStatus() == 200) {
                                if (sliderImage.getData().size() != 0) {
                                    Log.d(SliderImgTAG, "Responce Success: " + sliderImage.getData());
                                    sliderLiveData.postValue(Responce.success(sliderImage.getData()));
                                } else {
                                    sliderLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                                }
                            } else {
                                sliderLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(SliderImgTAG, e.getMessage());
                            sliderLiveData.postValue(Responce.error(e.getLocalizedMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(SliderImgTAG, "No Internet");
            sliderLiveData.postValue(Responce.internet(context.getString(R.string.no_internet_snackbar_text)));
        }
    }
}

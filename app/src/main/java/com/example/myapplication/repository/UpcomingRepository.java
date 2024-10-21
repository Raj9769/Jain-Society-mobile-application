package com.example.myapplication.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.EventModel;
import com.example.myapplication.model.EventSubModel;
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

public class UpcomingRepository {
    private static final String EventTAG = "Slider_TAG";
    private final Context context;
    private final MutableLiveData<Responce<List<EventSubModel>>> upComingLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public UpcomingRepository(Context context) {
        this.context = context;
    }

    public LiveData<Responce<List<EventSubModel>>> getUpComingLiveData() {
        return upComingLiveData;
    }

    public void getUpComingData() {
        if (NetworkUtils.isNetworkConnected(context)) {
            upComingLiveData.postValue(Responce.loading(null));
            Observable<EventModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                    .getUpcomingEventModel();

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EventModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull EventModel eventModel) {
                            if (eventModel.getStatus() == 200) {
                                if (eventModel.getData().size() != 0) {
                                    Log.d(EventTAG, "Responce Success: " + eventModel.getData());
                                    upComingLiveData.postValue(Responce.success(eventModel.getData()));
                                } else {
                                    upComingLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                                }
                            } else {
                                upComingLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(EventTAG, e.getMessage());
                            upComingLiveData.postValue(Responce.error(e.getLocalizedMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(EventTAG, "No Internet");
            upComingLiveData.postValue(Responce.internet(context.getString(R.string.no_internet_snackbar_text)));
        }
    }
}

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

public class EventRepository {
    private static final String EventTAG = "Slider_TAG";

    private final Context context;
    private final MutableLiveData<Responce<List<EventSubModel>>> eventLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public EventRepository(Context context) {
        this.context = context;
    }

    public LiveData<Responce<List<EventSubModel>>> getEventLiveData() {
        return eventLiveData;
    }

    public void getEventData() {
        if (NetworkUtils.isNetworkConnected(context)) {
            eventLiveData.postValue(Responce.loading(null));
            Observable<EventModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                    .getEventModel();

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
                                    eventLiveData.postValue(Responce.success(eventModel.getData()));
                                } else {
                                    eventLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                                }
                            } else {
                                eventLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(EventTAG, e.getMessage());
                            eventLiveData.postValue(Responce.error(e.getLocalizedMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(EventTAG, "No Internet");
            eventLiveData.postValue(Responce.internet(context.getString(R.string.no_internet_snackbar_text)));
        }
    }
}

package com.example.myapplication.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.MusicModel;
import com.example.myapplication.model.MusicSubModel;
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

public class MusicRepository {
    private static final String MusicTAG = "Music_TAG";
    private final Context context;
    private final MutableLiveData<Responce<List<MusicSubModel>>> musicLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MusicRepository(Context context) {
        this.context = context;
    }

    public LiveData<Responce<List<MusicSubModel>>> getMusicLiveData() {
        return musicLiveData;
    }

    public void getMusicData() {
        if (NetworkUtils.isNetworkConnected(context)) {
            musicLiveData.postValue(Responce.loading(null));
            Observable<MusicModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                    .getMusicModel();

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MusicModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull MusicModel musicModel) {
                            if (musicModel.getStatus() == 200) {
                                if (musicModel.getData().size() != 0) {
                                    Log.d(MusicTAG, "Responce Success: " + musicModel.getData());
                                    musicLiveData.postValue(Responce.success(musicModel.getData()));
                                } else {
                                    musicLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                                }
                            } else {
                                musicLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(MusicTAG, e.getMessage());
                            musicLiveData.postValue(Responce.error(e.getLocalizedMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(MusicTAG, "No Internet");
            musicLiveData.postValue(Responce.internet(context.getString(R.string.no_internet_snackbar_text)));
        }
    }
}

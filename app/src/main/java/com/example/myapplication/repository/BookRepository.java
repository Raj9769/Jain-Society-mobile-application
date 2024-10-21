package com.example.myapplication.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.BookModel;
import com.example.myapplication.model.EventModel;
import com.example.myapplication.model.BookSubModel;
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

public class BookRepository {
    private static final String BookTAG = "Book_TAG";

    private final Context context;
    private final MutableLiveData<Responce<List<BookSubModel>>> bookLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BookRepository(Context context) {
        this.context = context;
    }

    public LiveData<Responce<List<BookSubModel>>> getBookLiveData() {
        return bookLiveData;
    }

    public void getBookData() {
        if (NetworkUtils.isNetworkConnected(context)) {
            bookLiveData.postValue(Responce.loading(null));
            Observable<BookModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                    .getBookModel();

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BookModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull BookModel bookModel) {
                            if (bookModel.getStatus() == 200) {
                                if (bookModel.getData().size() != 0) {
                                    Log.d(BookTAG, "Responce Success: " + bookModel.getData());
                                    bookLiveData.postValue(Responce.success(bookModel.getData()));
                                } else {
                                    bookLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                                }
                            } else {
                                bookLiveData.postValue(Responce.error(context.getString(R.string.request_fail_err)));
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(BookTAG, e.getMessage());
                            bookLiveData.postValue(Responce.error(e.getLocalizedMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(BookTAG, "No Internet");
            bookLiveData.postValue(Responce.internet(context.getString(R.string.no_internet_snackbar_text)));
        }
    }
}

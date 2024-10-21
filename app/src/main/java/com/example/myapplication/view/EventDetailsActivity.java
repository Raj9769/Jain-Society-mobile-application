package com.example.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityEventDetailsBinding;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.model.LoginModel;
import com.example.myapplication.model.ParticipentModel;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EventDetailsActivity extends AppCompatActivity {

    private ActivityEventDetailsBinding binding;
    List<EventSubModel> list;
    private int position;
    private Context context;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        list = (List<EventSubModel>) getIntent().getSerializableExtra("list");
        position = getIntent().getIntExtra("position", 0);

        initView();
        initClickEvent();
    }

    private void initView() {
        binding.txtEnevetId.setText("Event " + list.get(position).getId());

        binding.txtTitleOffical.setText(list.get(position).getTitle());

        binding.txtDateTimeOffical.setText(list.get(position).getDate());

        binding.txtLocationOffical.setText(list.get(position).getLocation());

        Glide.with(this)
                .load("https://event.sevenstepsschool.org" + list.get(position).getFile())
                .centerCrop()
                .into(binding.eventImage);
    }

    private void initClickEvent() {
        binding.btnSubmit.setOnClickListener(view -> {
            int clientId = NetworkUtils.getSharedPreferenceStatus(getApplicationContext(),
                    StaticData.PREF_NAME, StaticData.USER_ID, 0);
            String clientName = NetworkUtils.getSharedPreferenceData(this,
                    StaticData.PREF_NAME, StaticData.USER_NAME, "");
            int eventId = list.get(position).getId();
            String eventTitle = binding.txtTitleOffical.getText().toString();
            String kinds = binding.editKidsMember.getText().toString();
            String adults = binding.editAdultMember.getText().toString();
            if (adults.isEmpty()) {
                binding.editAdultMember.setError("required.!");
            } else if (kinds.isEmpty()) {
                binding.editKidsMember.setError("required.!");
            } else {
                callApi(clientId, clientName, eventId, eventTitle, kinds, adults);
            }
        });

        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    //* call api *//
    private void callApi(int clientId, String clientName, int eventId, String eventTitle, String kinds, String adults) {
        alertDialog = NetworkUtils.createLoaderAlertDialog(this);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        RequestBody requestBody = RequestBody.create(String.valueOf(clientId), MediaType.parse("text/plain"));
        RequestBody requestBody1 = RequestBody.create(String.valueOf(eventId), MediaType.parse("text/plain"));
        RequestBody requestBody2 = RequestBody.create(clientName, MediaType.parse("text/plain"));
        RequestBody requestBody3 = RequestBody.create(eventTitle, MediaType.parse("text/plain"));
        RequestBody requestBody4 = RequestBody.create(kinds, MediaType.parse("text/plain"));
        RequestBody requestBody5 = RequestBody.create(adults, MediaType.parse("text/plain"));

        Observable<ParticipentModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                .getAddParticipent(requestBody, requestBody2, requestBody1, requestBody3, requestBody4, requestBody5);

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParticipentModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ParticipentModel participentModel) {
                        if (participentModel.getStatus() == 200) {
                            Toast.makeText(context, participentModel.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EventDetailsActivity.this, HomeActivity.class));
                            alertDialog.dismiss();
                            finishAffinity();
                        } else if (participentModel.getStatus() == 400) {
                            alertDialog.dismiss();
                            Toast.makeText(context, participentModel.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("ParticipentTAG", e.getMessage());
                        new Handler().postDelayed(() -> {
                            alertDialog.dismiss();
                            Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
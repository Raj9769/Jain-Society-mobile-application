package com.example.myapplication.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.example.myapplication.model.RegisterModel;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;
import com.onesignal.OneSignal;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {
    private static final String RegisterTAG = "register_TAG";
    private ActivityRegisterBinding binding;
    private Context context;
    private String Uuid;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        Uuid = OneSignal.getDeviceState().getUserId();
        initUi();
        initClickEvent();
    }

    //* initialize ui *//
    private void initUi() {

    }

    //* initialize click event *//
    private void initClickEvent() {
        binding.signUpButton.setOnClickListener(view -> {
            String userName = binding.userNameEditText.getText().toString();
            String userEmail = binding.emailEditText.getText().toString();
            String number = binding.mobileEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            String confPassword = binding.confPasswordEditText.getText().toString();
            if (userName == null || userName.equals("")) {
                NetworkUtils.setError("User name is required.!", binding.userNameEditText, binding.userNameInputLayout);
            } else if (userEmail == null || userEmail.equals("")) {
                NetworkUtils.setError("Email is required.!", binding.emailEditText, binding.emailInputLayout);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                NetworkUtils.setError("Invalid email.!", binding.emailEditText, binding.emailInputLayout);
            } else if (number == null || number.equals("")) {
                NetworkUtils.setError("Phone number.!", binding.mobileEditText, binding.mobileInputLayout);
            } else if (number.length() < 10) {
                NetworkUtils.setError("Invalid mobile number.!", binding.mobileEditText, binding.mobileInputLayout);
            } else if (password == null || password.equals("")) {
                NetworkUtils.setError("Password is required.!", binding.passwordEditText, binding.passwordInputLayout);
            } else if (confPassword == null || confPassword.equals("")) {
                NetworkUtils.setError("Conform password is required.!", binding.confPasswordEditText, binding.confPasswordInputLayout);
            } else if (!password.equals(confPassword)) {
                NetworkUtils.setError("Invalid conform password.!", binding.confPasswordEditText, binding.confPasswordInputLayout);
            } else {
                alertDialog = NetworkUtils.createLoaderAlertDialog(RegisterActivity.this);
                callApi(userName, userEmail, number, password, confPassword);
            }
        });
    }

    //* call register api *//
    private void callApi(String userName, String userEmail, String number, String password, String confPassword) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        RequestBody requestBody = RequestBody.create(Uuid,MediaType.parse("text/plain"));
        RequestBody requestBody1 = RequestBody.create(userName, MediaType.parse("text/plain"));
        RequestBody requestBody2 = RequestBody.create(userEmail, MediaType.parse("text/plain"));
        RequestBody requestBody3 = RequestBody.create(number, MediaType.parse("text/plain"));
        RequestBody requestBody4 = RequestBody.create(password, MediaType.parse("text/plain"));
        RequestBody requestBody5 = RequestBody.create(confPassword, MediaType.parse("text/plain"));
        Observable<RegisterModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                .getRegisterData(requestBody,requestBody1, requestBody2, requestBody3, requestBody4, requestBody5);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<RegisterModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull RegisterModel registerModel) {
                if (registerModel.getStatus() == 200) {
                    /* save client id to manually generated */
                    NetworkUtils.insertSharedPreferenceData(RegisterActivity.this,StaticData.PREF_NAME,StaticData.USER_ID,
                            registerModel.getData().getId());
                    Toast.makeText(context, registerModel.getMessage(), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finishAffinity();
                } else if (registerModel.getStatus() == 400) {
                    alertDialog.dismiss();
                    Toast.makeText(context, registerModel.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(RegisterTAG, e.getMessage());
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
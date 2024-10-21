package com.example.myapplication.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.example.myapplication.model.LoginModel;
import com.example.myapplication.model.RegisterModel;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {
    private static final String LoginTAG = "register_TAG";
    private ActivityLoginBinding binding;
    private Context context;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {

    }

    //* init click event *//
    private void initClickEvent() {
        binding.loginButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            if (email == null || email.equals("")) {
                NetworkUtils.setError("Email is required..!", binding.emailEditText, binding.emailInputLayout);
            } else if (password == null || password.equals("")) {
                NetworkUtils.setError("Password is required..!", binding.passwordEditText, binding.passwordInputLayout);
            } else {
                alertDialog = NetworkUtils.createLoaderAlertDialog(this);
                callLoginApi(email, password);
            }
        });

        binding.createAccountButton.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }

    //* call login api *//
    private void callLoginApi(String email, String password) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        RequestBody requestBody = RequestBody.create(email, MediaType.parse("text/plain"));
        RequestBody requestBody1 = RequestBody.create(password, MediaType.parse("text/plain"));
        Observable<LoginModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                .loginData(requestBody, requestBody1);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull LoginModel loginModel) {
                        if (loginModel.getStatus() == 200) {
                            NetworkUtils.insertSharedPreferenceData(LoginActivity.this, StaticData.PREF_NAME, StaticData.USER_ID, loginModel.getData().getId());
                            NetworkUtils.insertSharedPreferenceData(LoginActivity.this, StaticData.PREF_NAME, StaticData.USER_NAME, loginModel.getData().getName());
                            NetworkUtils.insertIsUserData(LoginActivity.this, StaticData.PREF_NAME, StaticData.IS_USER_LOGIN, true);
//                            Toast.makeText(context, loginModel.getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, loginModel.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            alertDialog.dismiss();
                            finishAffinity();
                        } else if (loginModel.getStatus() == 400) {
                            alertDialog.dismiss();
                            Toast.makeText(context, loginModel.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(LoginTAG, e.getMessage());
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
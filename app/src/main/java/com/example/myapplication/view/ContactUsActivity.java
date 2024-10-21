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
import com.example.myapplication.databinding.ActivityContactUsBinding;
import com.example.myapplication.model.ContactModel;
import com.example.myapplication.model.DonationModel;
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

public class ContactUsActivity extends AppCompatActivity {
    private static final String ContactTAG = "contact_TAG";
    private ActivityContactUsBinding binding;
    private Context context;
    private String name,mobile,email,description;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {
        
    }

    //* ini click event *//
    private void initClickEvent() {
        binding.backButton.setOnClickListener(view -> onBackPressed());
        
        binding.submitButton.setOnClickListener(view -> {
            name = binding.nameEditText.getText().toString();
            mobile = binding.mobileEditText.getText().toString();
            email = binding.emailEditText.getText().toString();
            description = binding.discEdittext.getText().toString();
            if (name == null || name.equals("")){
                NetworkUtils.setError("Name is required.!",binding.nameEditText,binding.nameInputLayout);
            } else if (mobile == null || mobile.equals("")) {
                NetworkUtils.setError("Mobile number is required.!",binding.mobileEditText,binding.mobileInputLayout);
            } else if (mobile.length() < 10) {
                NetworkUtils.setError("Invalid mobile number.!",binding.mobileEditText,binding.mobileInputLayout);
            } else if (email == null || email.equals("")){
                NetworkUtils.setError("Email is required.!",binding.emailEditText,binding.emailInputLayout);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                NetworkUtils.setError("Invalid email.!", binding.emailEditText, binding.emailInputLayout);
            } else if (description == null || description.equals("")){
                binding.discEdittext.setError("Description is required.!");
            } else {
                callApi();
            }

        });
        
    }

    private void callApi() {
        alertDialog = NetworkUtils.createLoaderAlertDialog(ContactUsActivity.this);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        RequestBody requestBody1 = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody requestBody2 = RequestBody.create(mobile, MediaType.parse("text/plain"));
        RequestBody requestBody3 = RequestBody.create(email, MediaType.parse("text/plain"));
        RequestBody requestBody4 = RequestBody.create(description, MediaType.parse("text/plain"));

        Observable<ContactModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                .getAddContact(requestBody1, requestBody2, requestBody3, requestBody4);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ContactModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull ContactModel donationModel) {
                if (donationModel.getStatus() == 200) {
                    alertDialog.dismiss();
                    NetworkUtils.showSnackbar(ContactUsActivity.this,binding.getRoot(),"Add inquiry successfully");
                    startActivity(new Intent(ContactUsActivity.this,HomeActivity.class));
                    finishAffinity();
                } else if (donationModel.getStatus() == 400) {
                    alertDialog.dismiss();
                    Toast.makeText(context, donationModel.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(ContactTAG, e.getMessage());
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
package com.example.myapplication.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.util.FormPreference;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;
import com.razorpay.Checkout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Context context;
    private boolean isUserLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        Checkout.preload(context);
        initUi();
        isUserLogin = NetworkUtils.getIsUserData(this,
                StaticData.PREF_NAME,
                StaticData.IS_USER_LOGIN);
    }

    private void initUi() {
        new Handler().postDelayed(() -> {
            if (!isUserLogin) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                finish();
            }
        },2000);
    }
}
package com.example.myapplication.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAboutUsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        initUi();
        initClickEvent();
    }

    //* Initialise Ui *//
    private void initUi() {
        //Read File
        InputStream is = getResources().openRawResource(R.raw.about_us);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        String entireFile = "";
        try {
            while((line = br.readLine()) != null) { // <--------- place readLine() inside loop
                entireFile += (line + "\n"); // <---------- add each line to entireFile
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.txtPrivacyPolicyData.setText(entireFile);
    }

    //* Initialize Click Event *//
    private void initClickEvent() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
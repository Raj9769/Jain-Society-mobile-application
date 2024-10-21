package com.example.myapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.EventAdapter;
import com.example.myapplication.adapter.SliderAdapter;
import com.example.myapplication.databinding.ActivityHomeBinding;
import com.example.myapplication.model.EventSubModel;
import com.example.myapplication.model.SliderSubImage;
import com.example.myapplication.repository.EventRepository;
import com.example.myapplication.repository.Responce;
import com.example.myapplication.repository.SliderImgRepository;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;
import com.example.myapplication.viewmodel.EventViewModel;
import com.example.myapplication.viewmodel.EventViewModelFactory;
import com.example.myapplication.viewmodel.SliderViewModel;
import com.example.myapplication.viewmodel.SliderViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String PERMISSION_TAG = "perTAG";
    private ActivityHomeBinding binding;
    private Context context;
    private AlertDialog alertDialog;
    private String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        initUi();
        initClickEvent();
    }

    //* init ui *//
    private void initUi() {
        callSliderImageApi();
    }

    //* call slider image api *//
    private void callSliderImageApi() {
        SliderImgRepository repository = new SliderImgRepository(context);
        SliderViewModel viewModel = new ViewModelProvider(this, new SliderViewModelFactory(repository)).get(SliderViewModel.class);

        viewModel.getSliderLiveData().observe(this, new Observer<Responce<List<SliderSubImage>>>() {
            @Override
            public void onChanged(Responce<List<SliderSubImage>> listResponce) {
                switch (listResponce.status) {
                    case INTERNET:
                        Toast.makeText(context, getString(R.string.no_internet_snackbar_text), Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        alertDialog = NetworkUtils.createLoaderAlertDialog(HomeActivity.this);
                        break;
                    case SUCCESS:
                        setSliderData(listResponce.data);
                        break;
                    case FAIL:
                        Toast.makeText(context, getString(R.string.request_fail_err), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //* set slider data *//
    private void setSliderData(List<SliderSubImage> data) {
        binding.slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        SliderAdapter adapter = new SliderAdapter(data, this);
        binding.slider.setSliderAdapter(adapter);
        binding.slider.setScrollTimeInSec(3);
        binding.slider.setAutoCycle(true);
        binding.slider.startAutoCycle();
        alertDialog.dismiss();
    }

    //* init click event *//
    private void initClickEvent() {
        binding.homeEventCard.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, EventActivity.class));
        });
        binding.homeBookCard.setOnClickListener(view -> {
            if (!hasPermission(HomeActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(HomeActivity.this, PERMISSIONS, 100);
            } else {
                startActivity(new Intent(HomeActivity.this, BookActivity.class));
            }
        });
        binding.homeMusicCard.setOnClickListener(view -> {
            if (!hasPermission(HomeActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(HomeActivity.this, PERMISSIONS, 100);
            } else {
                startActivity(new Intent(HomeActivity.this, MusicActivity.class));
            }
        });
        binding.homeDonationCard.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, DonationActivity.class));
        });
        binding.homeUpcomingEventButton.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, UpcomingActivity.class));
        });

        binding.homeAboutUsCard.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, AboutUsActivity.class)));

        binding.homeContactCard.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ContactUsActivity.class)));

        binding.homeLogoutBtn.setOnClickListener(view -> {
            NetworkUtils.insertIsUserData(HomeActivity.this, StaticData.PREF_NAME, StaticData.IS_USER_LOGIN, false);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }

    //* Check All Permission *//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Camera Request Permission ===============================================================
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d(PERMISSION_TAG, "Permission granted");
            } else {
                Log.d(PERMISSION_TAG, "Permission denied");
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        //do something here.
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Storage permissions are required.!", Snackbar.LENGTH_LONG).setAction("setting", view -> settingPermissionOpen());
                        snackbar.setTextColor(getResources().getColor(R.color.black));
                        snackbar.setActionTextColor(getResources().getColor(R.color.taskbar_color));
                        snackbar.show();
                    }
                }
            }
        }
    }

    //* permission check *//
    public boolean hasPermission(Context context, String[] PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Setting Permission ==========================================================================
    public void settingPermissionOpen() {
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(settingIntent);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
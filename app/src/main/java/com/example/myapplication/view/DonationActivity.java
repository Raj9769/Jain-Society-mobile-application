package com.example.myapplication.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityDonationBinding;
import com.example.myapplication.databinding.DialogSuccessBinding;
import com.example.myapplication.model.DonationModel;
import com.example.myapplication.util.NetworkUtils;
import com.example.myapplication.util.StaticData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class DonationActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String DonationTAG = "donation_TAG";
    private ActivityDonationBinding binding;
    private AlertDialog alertDialog;
    private Context context;
    private String donationType;
    private int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        initUi();
        initClickEvent();

    }

    //* init ui *//
    private void initUi() {
        clientId = NetworkUtils.getSharedPreferenceStatus(this,
                StaticData.PREF_NAME,StaticData.USER_ID,0);
    }

    private void typeSelected(){
        int selectedId = binding.helthInsChipGroup.getCheckedChipId();
        if (selectedId == R.id.moneyChip1) {
            donationType = "Money";
        } else if (selectedId == R.id.clothsChip2) {
            donationType = "Cloths";
        } else if (selectedId == R.id.foodChip3) {
            donationType = "Donation";
        } else if (selectedId == R.id.homeChip4) {
            donationType = "Home Accessories";
        } else if (selectedId == R.id.otherChip5) {
            donationType = "Other";
        }
    }

    //* init click event *//
    private void initClickEvent() {
        binding.donationSubmitButton.setOnClickListener(view -> {
            typeSelected();
            String name = binding.nameEditText.getText().toString();
            String mobileNo = binding.mobileEditText.getText().toString();
            String notes = binding.notesTypeEditText.getText().toString();
            if (name == null || name.equals("")){
                NetworkUtils.setError("User name is required..!",binding.nameEditText,binding.nameInputLayout);
            } else if (mobileNo == null || mobileNo.equals("")) {
                NetworkUtils.setError("Mobile number is required.!",binding.mobileEditText,binding.mobileInputLayout);
            } else if (mobileNo.length() < 10) {
                NetworkUtils.setError("Invalid mobile number.!", binding.mobileEditText, binding.mobileInputLayout);
            } else if (notes == null || notes.equals("")) {
                Toast.makeText(context, "Notes message is required.!", Toast.LENGTH_SHORT).show();
            } else if (donationType == null || donationType.equals("")) {
                NetworkUtils.showSnackbar(DonationActivity.this,binding.getRoot(),"Please select donation type.!");
            } else {
                alertDialog = NetworkUtils.createLoaderAlertDialog(DonationActivity.this);
                callDonationApi(name,mobileNo,notes);
            }
        });

        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    //* donation api call *//
    private void callDonationApi(String name, String mobileNo, String notes) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        RequestBody requestBody = RequestBody.create(String.valueOf(clientId), MediaType.parse("text/plain"));
        RequestBody requestBody1 = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody requestBody2 = RequestBody.create(mobileNo, MediaType.parse("text/plain"));
        RequestBody requestBody3 = RequestBody.create(notes, MediaType.parse("text/plain"));
        RequestBody requestBody4 = RequestBody.create(donationType, MediaType.parse("text/plain"));

        Observable<DonationModel> observable = ApiService.getClient(StaticData.API_BASE_URL)
                .getDonationData(requestBody,requestBody1, requestBody2, requestBody3, requestBody4);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DonationModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull DonationModel donationModel) {
                if (donationModel.getStatus() == 200) {
                    alertDialog.dismiss();
                    makePayment(donationModel.getData().getUsername()
                    ,donationModel.getData().getEmail(), donationModel.getData().getPhone(),
                            donationModel.getData().getAmount());
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
                Log.d(DonationTAG, e.getMessage());
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

    private void makePayment(String username, String email, String phone, Integer amount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_v546duH1t9RznD");

        checkout.setImage(R.drawable.logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", username);
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "CAD");
            options.put("amount", "30000");//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact",phone);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    //* show success dialog *//
    private void showSuccessDialog() {
        DialogSuccessBinding successBinding = DialogSuccessBinding.inflate(getLayoutInflater());
        AlertDialog successDialog = new MaterialAlertDialogBuilder(DonationActivity.this,R.style.AlertTheme)
                .setView(successBinding.getRoot())
                .setCancelable(false)
                .create();

        successBinding.materialButton.setOnClickListener(view -> {
            startActivity(new Intent(DonationActivity.this,HomeActivity.class));
            finishAffinity();
        });

        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.show();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(context, "Successful payment...", Toast.LENGTH_SHORT).show();
        showSuccessDialog();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(context, "Failed and cause is : "+s, Toast.LENGTH_SHORT).show();
    }
}
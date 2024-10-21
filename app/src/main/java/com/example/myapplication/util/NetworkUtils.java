package com.example.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NetworkUtils {
    //* Check internet *//
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //* Snack bar Show *//
    public static void showSnackbar(Activity activity, View view, String errorMessage) {
        Snackbar snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(activity.getResources().getColor(R.color.white, activity.getTheme()));
        snackbar.setBackgroundTint(activity.getResources().getColor(R.color.gry, activity.getTheme()));
        snackbar.setActionTextColor(activity.getResources().getColor(R.color.white, activity.getTheme()));
        snackbar.show();
    }

    //* Shared Preference Data Set *//
    public static void insertSharedPreferenceData(Activity context, String prefName, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, 0).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    //* Shared Preference Data Set *//
    public static void insertSharedPreferenceData(Activity context, String prefName, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, 0).edit();
        editor.putString(key, value);
        editor.apply();
    }

    //* Shared Preference Local Data Get *//
    public static String getSharedPreferenceData(Activity context, String prefName, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, 0);
        String data = sharedPreferences.getString(key, defaultValue);
        return data;
    }

    //* Shared Preference Local Data Get *//
    public static int getSharedPreferenceStatus(Context context, String databaseName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(databaseName, 0);
        int status = sharedPreferences.getInt(key, defaultValue);
        return status;
    }

    //* Shared Preference Data Set *//
    public static void insertIsUserData(Activity context, String prefName, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, 0).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    //* Shared Preference Data Set *//
    public static boolean getIsUserData(Activity context, String prefName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, 0);
        boolean data = sharedPreferences.getBoolean(key, false);
        return data;
    }

    //* Set Error After Any Field Empty *//
    public static void setError(String error, TextInputEditText editText, TextInputLayout textInputLayout) {
        textInputLayout.setError(error);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayout.setErrorEnabled(false);
            }
        });
    }

    //* Loader alert dialog *//
    public static AlertDialog createLoaderAlertDialog(Activity activity) {
        //Custom layout set
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.ad_dilog_loader, null);

        //Alert dialog
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(activity, R.style.AlertTheme)
                .setView(view)
                .setCancelable(false)
                .create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }

    //* set Custom crome *//
    public static void setCustomIntent(Activity activity, String gameUrl) {
        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
        // below line is setting toolbar color
        // for our custom chrome tab.
        CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                .setNavigationBarColor(ContextCompat.getColor(activity, R.color.toolbar_color))
                .setToolbarColor(ContextCompat.getColor(activity, R.color.toolbar_color))
                .setSecondaryToolbarColor(ContextCompat.getColor(activity, R.color.taskbar_color))
                .build();
        customIntent.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, params);

        // we are calling below method after
        // setting our toolbar color.
        setCustomChromeTab(activity, customIntent.build(), gameUrl);


    }

    //* Open Any Ads Click Event To Open Chrome *//
    public static void setCustomChromeTab(Activity activity, CustomTabsIntent customTabsIntent, String url) {
        String packageName = "com.android.chrome";
        String BASE_URL = "https://event.sevenstepsschool.org";
        //        Uri uri = Uri.parse(StaticData.URL);
        Uri uri = Uri.parse(BASE_URL+url);
        if (packageName != null) {

            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName);

            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri);
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

}

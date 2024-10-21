package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

public class FormPreference {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "FirstTimePre";
    private static final String IS_USER_FIRST_TIME = "userFirstTime";

    public FormPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setIsUserFirstTime(boolean firstTimeLaunch) {
        editor.putBoolean(IS_USER_FIRST_TIME, firstTimeLaunch);
        editor.commit();
    }

    public boolean isUserFirstTime() {
        return preferences.getBoolean(IS_USER_FIRST_TIME, true);
    }
}

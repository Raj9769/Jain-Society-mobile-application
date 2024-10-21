package com.example.myapplication.util;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

public class MainApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "cf248abb-a762-4661-8245-a0337c7fe4ac";

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(context);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
    }
}

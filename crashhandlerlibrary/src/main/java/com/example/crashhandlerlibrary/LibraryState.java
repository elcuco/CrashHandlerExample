package com.example.crashhandlerlibrary;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class LibraryState {
    public String baseurl;
    Thread.UncaughtExceptionHandler old;
    boolean disabled;
    Application userApp;
    NetworkUploader uploader;

    LibraryState(Application app) {
        userApp = app;
        disabled = false;
        baseurl = "http://192.168.1.222:9000/api/exceptions/";
    }

    void registerAlarm() {
        // Each and every 15 minutes will trigger onReceive of your BroadcastReceiver
        long our_interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long first_trigger = System.currentTimeMillis() + our_interval;

        mPingAlarmPendIntent = PendingIntent.getBroadcast(userApp, 0, mPingAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) userApp.getSystemService(Context.ALARM_SERVICE))
                .setInexactRepeating(AlarmManager.RTC_WAKEUP, first_trigger, our_interval, mPingAlarmPendIntent);

        userApp.registerReceiver(mPingAlarmReceiver, new IntentFilter(PING_ALARM));
    }

    void unregisterAlarm() {
        ((AlarmManager) userApp.getSystemService(Context.ALARM_SERVICE)).cancel(mPingAlarmPendIntent);
        userApp.unregisterReceiver(mPingAlarmReceiver);
    }

    private static final String PING_ALARM = "com.example.crashhandlerlibrary.PING_ALARM";
    private PendingIntent mPingAlarmPendIntent;
    private final Intent mPingAlarmIntent = new Intent(PING_ALARM);
    private final BroadcastReceiver mPingAlarmReceiver = new UploadCrashesReceiver(this);
}


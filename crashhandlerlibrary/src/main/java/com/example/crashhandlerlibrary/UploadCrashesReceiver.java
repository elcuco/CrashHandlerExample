package com.example.crashhandlerlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UploadCrashesReceiver extends BroadcastReceiver {
    private static final String TAG = "CrashTimer";
    LibraryState libraryState;
    public UploadCrashesReceiver(LibraryState libraryState) {
        this.libraryState = libraryState;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Trying to upload crashes");
        ExceptionStorage.uploadCrashes(libraryState);
    }
}

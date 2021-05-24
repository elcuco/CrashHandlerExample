package com.example.crashhandlerlibrary;

import android.app.Application;

class LibraryState {
    public String baseurl;
    Thread.UncaughtExceptionHandler old;
    boolean disabled;
    Application userApp;
    NetworkUploader uploader;

    LibraryState(Application app) {
        userApp = app;
        disabled = false;
        baseurl = "http://192.168.1.222:8000/api/exceptions/";
    }
}

package com.example.crashhandlerlibrary;

import android.app.Application;

class LibraryState {
    Thread.UncaughtExceptionHandler old;
    boolean disabled;
    Application userApp;

    LibraryState(Application app) {
        userApp = app;
        disabled = false;
    }
}

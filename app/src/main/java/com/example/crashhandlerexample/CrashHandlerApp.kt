package com.example.crashhandlerexample

import android.app.Application
import com.example.crashhandlerlibrary.CrashHandlerLibrary

class CrashHandlerApp : Application() {
    init {
        CrashHandlerLibrary.setup(this)
    }
}

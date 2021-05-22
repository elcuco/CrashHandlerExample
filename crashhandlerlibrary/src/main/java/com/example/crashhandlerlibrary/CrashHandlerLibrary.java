package com.example.crashhandlerlibrary;

import android.app.Application;

public class CrashHandlerLibrary {
    private static LibraryState data;

    // In theory I should save the old handler, and then restore on uninstall
    // However - WTF would you want to uninstall this?
    // Also -  if we uninstall the crash handlers out of order (install c1->c2->c3,
    // and then uninstall c3->c1->c2) - the wrong crash handler will be set anyway,
    // and we might (will) have a reference to something that is not valid.
    // Other libraries will do this - but this is not something we can fix - so not.
    // Lets not even try. Java is broken (it should append to the list of exception
    // handlers, and not set a global one.
    // TLDR: we don't have "unsetup" or similar

    public static void setup(Application app)
    {
        data = new LibraryState(app);
        data.old = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(data));
    }
}

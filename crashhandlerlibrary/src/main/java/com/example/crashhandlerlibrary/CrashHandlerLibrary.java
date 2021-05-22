package com.example.crashhandlerlibrary;

import android.app.Application;

import java.io.File;

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

    // I admit this will not always work... but its a nice hack :)
    // I can use theoritycally safetynet, but I don't want to import GooglePlay seervices
    // libraries into this library. I want this to be a self contained library.
    @SuppressWarnings("RedundantIfStatement")
    public static boolean isRooted(Application userApp) {
        if (findBinary("su"))
            return true;
        if (findBinary("Superuser.apk"))
            return true;
        if (findBinary("superuser.apk"))
            return true;
        return false;
    }

    // http://www.codeplayon.com/2020/07/android-how-to-check-phone-rooted-or-not/
    private static boolean findBinary(String binaryName) {
        boolean found = false;
        String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/",
                "/system/app/",
                "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                found = true;
                break;
            }
        }
        return found;
    }
}

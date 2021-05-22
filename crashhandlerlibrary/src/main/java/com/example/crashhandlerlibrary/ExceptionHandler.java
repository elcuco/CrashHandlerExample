package com.example.crashhandlerlibrary;

import androidx.annotation.NonNull;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    LibraryState state;
    public ExceptionHandler(LibraryState libraryState) {
        state = libraryState;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        //noinspection finally
        try {
            // This should not happen, but a crash handler should be super stable
            if (state == null) {
                return;
            }

            if (state.disabled) {
                return;
            }

            // just in case we do fail later on - lets disable the crash handling
            // no - we cannot uninstall the crash handler, since another one might
            // be installed after us, and when that one will uninstall - we will get a
            // dangling exception handler.
            // This is a design flaw in Java - it should append to the list of crash handler
            // and not maintain a global one.
            state.disabled = true;

            ExceptionStorage.save(state, t, e);

            if (state.old != null) {
                state.old.uncaughtException(t, e);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            System.exit(1);
        }
    }
}

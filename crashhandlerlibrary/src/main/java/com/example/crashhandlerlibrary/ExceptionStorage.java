package com.example.crashhandlerlibrary;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class ExceptionStorage {
    public static void save(LibraryState state, Thread t, Throwable e) throws JSONException, IOException {
        long now = System.currentTimeMillis();
        JSONObject o = new JSONObject();
        o.put("thread", getThreadAsJson(t));
        o.put("throwable", getThowableAsJson(e));
        o.put("appdetails", getAppDetails(state));
        o.put("epoch", now);

        String s = o.toString(4);
        String filename = String.format(Locale.ENGLISH, "%d-crash.log", now);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(state.userApp.openFileOutput(filename, Context.MODE_PRIVATE));
        outputStreamWriter.write(s);
        outputStreamWriter.close();

        Log.d("CRASH", "----------------");
        Log.d("CRASH", String.format(Locale.ENGLISH, "Crash details (%d)", s.length()));
        Log.d("CRASH", s);
        Log.d("CRASH", "----------------");
    }

    // We can introduce much more data as neede, this is just a base reference
    private static JSONObject getAppDetails(LibraryState state) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("packageName", state.userApp.getPackageName());
        o.put("rooted", CrashHandlerLibrary.isRooted(state.userApp));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            o.put("android", Build.VERSION.BASE_OS);
            o.put("android.patch", Build.VERSION.SECURITY_PATCH);
        }
        o.put("SDK", Build.VERSION.SDK_INT);
        o.put("manufacturer", Build.MANUFACTURER);
        o.put("model", Build.MODEL);
        o.put("ID", Build.ID);
        return o;
    }

    private static JSONObject getThowableAsJson(Throwable e) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("message", e.getMessage());
        o.put("stack", getStackAsJson(e.getStackTrace()));
        return o;
    }

    private static JSONObject getThreadAsJson(Thread t) {
        JSONObject o = new JSONObject();
        try {
            o.put("name", t.getName());
            o.put("state", t.getState().toString());
            o.put("alive", t.isAlive());
            o.put("interrupted", t.isInterrupted());
            o.put("Matt", t.isDaemon());
//            Does this help? It shows the current stack trace
//            o.put("stack", getStackAsJson(t.getStackTrace()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    // here is where Kotlin class extensions would be great.
    // But I made a boo-boo and decided to do this in java. Shame. Shame.
    private static JSONArray getStackAsJson(StackTraceElement[] stackTrace) throws JSONException {
        JSONArray a = new JSONArray();
        for (StackTraceElement s: stackTrace ) {
            a.put(StackTraceElementToJson(s));
        }
        return a;
    }

    // here is where Kotlin class extensions would be great.
    // But I made a boo-boo and decided to do this in java. Shame. Shame.
    private static JSONObject StackTraceElementToJson(StackTraceElement s) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("class", s.getClassName());
        o.put("file", s.getFileName());
        o.put("line", s.getLineNumber());
        o.put("method", s.getMethodName());
        o.put("native", s.isNativeMethod());
        return o;
    }
}

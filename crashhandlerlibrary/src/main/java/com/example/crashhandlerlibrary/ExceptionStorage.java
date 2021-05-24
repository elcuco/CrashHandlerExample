package com.example.crashhandlerlibrary;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.Stack;

public class ExceptionStorage {
    private static final String TAG = "CrashStorage";

    public static void save(LibraryState state, Thread t, Throwable e) throws JSONException, IOException {
        long now = System.currentTimeMillis();
        JSONObject o = new JSONObject();
        o.put("thread", getThreadAsJson(t));
        o.put("throwable", getThowableAsJson(e));
        o.put("appDetails", getAppDetails(state));
        o.put("epoch", now);

        String s = o.toString();
        String filename = String.format(Locale.ENGLISH, "%d-crash.log", now);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(state.userApp.openFileOutput(filename, Context.MODE_PRIVATE));
        outputStreamWriter.write(s);
        outputStreamWriter.close();

        Log.d(TAG, String.format(Locale.ENGLISH, "Crash saved, %d bytes", s.length()));
    }

    // https://expressjs.com/en/api.html#express.json
    // upload up to 70K, to allow default express JSON parser to be happy
    private static final long MAX_SIZE = 50 * 1024;

    public static void discardLocalCrashes(LibraryState data) {
        File[] files;
        try {
            FileFilter filter = f -> f.getName().endsWith("-crash.log");
            File dir = data.userApp.getFilesDir();
            files = dir.listFiles(filter);
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadCrashes(LibraryState data) {
        Log.d(TAG, "Startup upload");
        File[] files;
        try {
            FileFilter filter = f -> f.getName().endsWith("-crash.log");
            File dir = data.userApp.getFilesDir();
            files = dir.listFiles(filter);
            Log.d(TAG, "Found total files: " + files.length);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (files == null) {
            Log.d(TAG, "Nothing to upload");
            return;
        }

        if (data.uploader == null) {
            Log.d(TAG, "No network uploader, refuse to do anything");
            return;
        }

        Stack<File> s = new Stack<>();
        String json = null;
        try {
            JSONObject o = generateJsonFromFiles(files, s);
            json = o.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d(TAG, String.format(Locale.ENGLISH, "Uploading %d files, %d bytes", s.size(), json.length()));
        if (s.size() == 0) {
            Log.d(TAG, "Nothing to upload");
            return;
        }
        data.uploader.upload(data.baseurl, json, new NetworkUploaderResult() {
            @Override
            public void onSuccess() {
                long totalLogs = s.size();
                long deletedLogs = 0;
                while (!s.empty()) {
                    File f = s.pop();
                    if (!f.delete()) {
                        Log.d(TAG, "Failed deleting " + f.getName());
                    } else {
                        deletedLogs++;
                    }
                }
                Log.d(TAG, String.format(Locale.ENGLISH, "Deleted %d/%d files", deletedLogs, totalLogs));
            }

            @Override
            public void onFail() {
            }
        });
    }

    private static JSONObject generateJsonFromFiles(File[] files, Stack<File> processedFiles) throws JSONException {
        JSONObject o = new JSONObject();
        JSONArray dumps = new JSONArray();
        long uploadSize = 0;
        for (File f : files) {
            long l = f.length();
            long max = MAX_SIZE - l;
            if (uploadSize < max) {
                try {
                    String contents = readFile(f);
                    dumps.put(new JSONObject(contents));
                    processedFiles.push(f);
                } catch (IOException | JSONException e) {
                    boolean d = f.delete();
                    if (!d) {
                        Log.d(TAG, "Failed deleting " + f.getName());
                    }
                }
            } else {
                break;
            }
        }
        o.put("dumps", dumps);
        return o;
    }

    private static String readFile(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }
        br.close();
        return sb.toString();
    }

    // We can introduce much more data as needed, this is just a base reference
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
        for (StackTraceElement s : stackTrace) {
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

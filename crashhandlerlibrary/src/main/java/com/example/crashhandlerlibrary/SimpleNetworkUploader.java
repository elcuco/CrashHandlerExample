package com.example.crashhandlerlibrary;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleNetworkUploader implements NetworkUploader {
    MyAsync myTask;

    public SimpleNetworkUploader() {
        myTask = new MyAsync();
    }

    @Override
    public void upload(@NonNull String url, @NonNull String data, @NonNull NetworkUploaderResult result) {
        //noinspection unchecked
        myTask.execute(url, data, result);
    }

    // Code borrowed from :
    // https://www.smashingmagazine.com/2017/03/simplify-android-networking-volley-http-library/
    @SuppressWarnings("deprecation")
    private static class MyAsync extends AsyncTask {
        @Override
        protected Integer doInBackground(Object... params) {
            NetworkUploaderResult r = (NetworkUploaderResult) (params[2]);
            try {
                HttpURLConnection connection;
                connection = (HttpURLConnection) new URL((String) params[0]).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                OutputStreamWriter outputStream = new OutputStreamWriter(connection.getOutputStream());
                outputStream.write((String) params[1]);
                outputStream.flush();
                outputStream.close();

                if (connection.getResponseCode() == 200) {
                    r.onSuccess();
                } else {
                    r.onFail();
                }

                return connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            r.onFail();
            return -1;
        }
    }
}

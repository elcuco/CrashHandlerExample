package com.example.crashhandlerlibrary;

import androidx.annotation.NonNull;

public interface NetworkUploader {
    void upload(@NonNull String url, @NonNull String data, @NonNull NetworkUploaderResult result);
}

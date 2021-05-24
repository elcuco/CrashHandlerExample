package com.example.crashhandlerexample

import android.app.Application
import com.example.crashhandlerlibrary.CrashHandlerLibrary
import com.example.crashhandlerlibrary.NetworkUploader
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class CrashHandlerApp : Application() {
    private val client = OkHttpClient()

    init {
    }

    override fun onCreate() {
        super.onCreate()

        val uploader = NetworkUploader { url, data, result ->
            print("Uploading to $url -> ${data.length} bytes")
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url(url)
                .post(data.toRequestBody(mediaType))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (response.isSuccessful)
                            result.onSuccess()
                        else
                            result.onFail()
                    }
                }
            })
        }

        CrashHandlerLibrary.setup(this)
        CrashHandlerLibrary.setUploader(uploader)
        CrashHandlerLibrary.uploadCrashes()
//      This also exists, but its not very useful
//      CrashHandlerLibrary.discardLocalCrashes()
    }
}

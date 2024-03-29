package com.example.crashhandlerexample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onThrowCatchClick(v: View) {
        try {
            throw Exception("Everything is fine")
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun onThrowFialClick(v: View) {
        throw Exception("Maximum effort")
    }
}
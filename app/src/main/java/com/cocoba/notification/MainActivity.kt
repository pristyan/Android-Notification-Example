package com.cocoba.notification

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getFcmToken()
    }

    private fun getFcmToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.e(TAG, it.token)
        }
    }
}

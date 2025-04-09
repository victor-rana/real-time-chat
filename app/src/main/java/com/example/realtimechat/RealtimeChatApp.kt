package com.example.realtimechat

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RealtimeChatApp : Application(){
    override fun onCreate() {
        super.onCreate()
       FirebaseApp.initializeApp(this)
    }
}
package com.school.management.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchoolApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

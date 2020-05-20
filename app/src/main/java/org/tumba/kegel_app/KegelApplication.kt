package org.tumba.kegel_app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class KegelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
package org.tumba.kegel_app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.tumba.kegel_app.di.initAppScope

class KegelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initAppScope(this)
    }
}
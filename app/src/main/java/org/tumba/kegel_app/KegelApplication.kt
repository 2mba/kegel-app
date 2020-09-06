package org.tumba.kegel_app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.tumba.kegel_app.di.Di
import org.tumba.kegel_app.di.component.DaggerAppComponent
import org.tumba.kegel_app.di.module.AppModule

class KegelApplication : Application() {

    private val component = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
        .also { Di.init(it) }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
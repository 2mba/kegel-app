package org.tumba.kegel_app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.tumba.kegel_app.di.component.DaggerAppComponent
import org.tumba.kegel_app.di.module.AppModule
import org.tumba.kegel_app.worker.NotifierWorkerManager
import javax.inject.Inject

class KegelApplication : Application() {

    val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    @Inject
    lateinit var notifierWorkerManager: NotifierWorkerManager

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        notifierWorkerManager.onStartApp()
    }
}
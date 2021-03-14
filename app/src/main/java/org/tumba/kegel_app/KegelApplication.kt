package org.tumba.kegel_app

import android.app.Application
import org.tumba.kegel_app.di.component.DaggerAppComponent
import org.tumba.kegel_app.di.module.AppModule

class KegelApplication : Application() {

    val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }
}
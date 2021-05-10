package org.tumba.kegel_app

import android.app.Application
import org.tumba.kegel_app.billing.BillingManager
import org.tumba.kegel_app.di.component.DaggerAppComponent
import org.tumba.kegel_app.di.module.AppModule
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import javax.inject.Inject

class KegelApplication : Application() {

    val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    @Inject
    lateinit var billingManager: BillingManager

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        settingsInteractor.restoreNightMode()
        billingManager.onAppStarted()
    }
}
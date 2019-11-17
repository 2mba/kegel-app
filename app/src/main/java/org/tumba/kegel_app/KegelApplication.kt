package org.tumba.kegel_app

import android.app.Application
import org.tumba.kegel_app.di.initAppScope

class KegelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAppScope(this)
    }
}
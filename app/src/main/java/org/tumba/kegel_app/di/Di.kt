package org.tumba.kegel_app.di

import org.tumba.kegel_app.di.component.AppComponent

object Di {
    private var _appComponent: AppComponent? = null
    val appComponent: AppComponent
        get() = _appComponent!!

    fun init(appComponent: AppComponent) {
        _appComponent = appComponent
    }
}
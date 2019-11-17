package org.tumba.kegel_app.di

import android.app.Application
import android.content.Context
import org.tumba.kegel_app.di.Scope.SCOPE_APP
import toothpick.Toothpick
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module

object Scope {
    const val SCOPE_APP = "SCOPE_APP"
    const val SCOPE_EXERCISE = "SCOPE_EXERCISE"
}

fun initAppScope(application: Application) {
    Toothpick
        .openScope(SCOPE_APP)
        .installModules(getAppModule(application))
}

private fun getAppModule(application: Application) = module {
    bind(Application::class).toInstance(application)
    bind(Context::class).toInstance(application)
}
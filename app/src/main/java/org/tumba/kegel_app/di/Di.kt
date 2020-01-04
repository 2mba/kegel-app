package org.tumba.kegel_app.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import org.tumba.kegel_app.core.system.*
import org.tumba.kegel_app.di.Scope.SCOPE_APP
import toothpick.Toothpick
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import toothpick.ktp.binding.toClass

object Scope {
    const val SCOPE_APP = "SCOPE_APP"
    const val SCOPE_EXERCISE = "SCOPE_EXERCISE"
    const val SCOPE_HOME = "SCOPE_HOME"
}

fun initAppScope(application: Application) {
    Toothpick
        .openScope(SCOPE_APP)
        .installModules(
            getAppModule(application),
            preferencesModule()
        )
}

private fun getAppModule(application: Application) = module {
    bind(Application::class).toInstance(application)
    bind(Context::class).toInstance(application)
    bind(Resources::class).toInstance(application.resources)
    bind(IResourceProvider::class).toClass(ResourceProviderImpl::class)
    bind(IVibrationManager::class).toClass(VibrationManager::class)
}

private fun preferencesModule() = module {
    bind(IPreferences::class).toClass(Preferences::class)
    bind(SharedPreferences::class).toProvider(AppSharedPreferencesProvider::class.java)
}
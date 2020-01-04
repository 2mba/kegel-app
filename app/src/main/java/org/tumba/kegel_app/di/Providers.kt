package org.tumba.kegel_app.di

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Provider

class AppSharedPreferencesProvider @Inject constructor(
    private val context: Context
) : Provider<SharedPreferences> {

    companion object {
        private const val APP_PREFERENCES_NAME = "APP_PREFERENCES_NAME"
    }

    override fun get(): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}
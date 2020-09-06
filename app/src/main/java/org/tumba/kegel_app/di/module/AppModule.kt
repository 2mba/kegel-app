package org.tumba.kegel_app.di.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.KegelApplication
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.core.system.ResourceProviderImpl
import org.tumba.kegel_app.service.ExerciseServiceNotificationProvider
import javax.inject.Singleton

@Module
class AppModule(private val application: KegelApplication) {

    @Provides
    fun provideApplication(): KegelApplication = application

    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideResourceProvider(context: Context): ResourceProvider = ResourceProviderImpl(context.applicationContext.resources)

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideExerciseServiceNotificationProvider(context: Context): ExerciseServiceNotificationProvider {
        return ExerciseServiceNotificationProvider(context.applicationContext)
    }

    companion object {
        private const val APP_PREFERENCES_NAME = "APP_PREFERENCES_NAME"
    }
}
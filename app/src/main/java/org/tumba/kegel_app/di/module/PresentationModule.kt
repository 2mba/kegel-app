package org.tumba.kegel_app.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.core.system.VibrationManagerImpl
import org.tumba.kegel_app.service.ExerciseService
import org.tumba.kegel_app.service.ExerciseServiceProxy
import org.tumba.kegel_app.ui.home.ProgressViewedStore
import javax.inject.Singleton

@Module
class PresentationModule {

    @Provides
    fun provideExerciseService(context: Context): ExerciseService {
        return ExerciseServiceProxy(context.applicationContext)
    }

    @Provides
    fun provideVibrationManager(context: Context): VibrationManager {
        return VibrationManagerImpl(context.applicationContext)
    }

    @Singleton
    @Provides
    fun provideProgressViewedStore(): ProgressViewedStore {
        return ProgressViewedStore()
    }

}
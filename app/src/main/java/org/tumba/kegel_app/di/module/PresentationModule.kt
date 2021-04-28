package org.tumba.kegel_app.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.core.system.SoundManager
import org.tumba.kegel_app.core.system.SoundManagerImpl
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.core.system.VibrationManagerImpl
import org.tumba.kegel_app.domain.ExerciseEffectsHandler
import org.tumba.kegel_app.domain.ExerciseEffectsHandlerImpl
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

    @Provides
    fun provideSoundManager(soundManagerImpl: SoundManagerImpl): SoundManager = soundManagerImpl

    @Provides
    fun provideExerciseEffectsHandler(exerciseEffectsHandler: ExerciseEffectsHandlerImpl): ExerciseEffectsHandler {
        return exerciseEffectsHandler
    }
}
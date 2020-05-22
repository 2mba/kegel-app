package org.tumba.kegel_app.utils

import android.content.Context
import android.content.SharedPreferences
import org.tumba.kegel_app.core.system.Preferences
import org.tumba.kegel_app.core.system.ResourceProviderImpl
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.exercise.ExerciseViewModelFactory
import org.tumba.kegel_app.ui.home.HomeViewModelFactory


object InjectorUtils {
    private const val APP_PREFERENCES_NAME = "APP_PREFERENCES_NAME"

    fun provideExerciseViewModelFactory(
        context: Context
    ): ExerciseViewModelFactory {
        val preferences = Preferences(provideSharedPreferences(context))
        return ExerciseViewModelFactory(
            ExerciseInteractor(
                ExerciseRepository(),
                ExerciseSettingsRepository(
                    preferences
                ),
                VibrationManager(context)
            ),
            ExerciseSettingsRepository(
                preferences
            ),
            ResourceProviderImpl(context.resources)
        )
    }

    fun provideHomeViewModelFactory(
        context: Context
    ): HomeViewModelFactory {
        val preferences = Preferences(provideSharedPreferences(context))
        return HomeViewModelFactory(
            ExerciseSettingsRepository(
                preferences
            )
        )
    }

    private fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}

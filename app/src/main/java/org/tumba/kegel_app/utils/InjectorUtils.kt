package org.tumba.kegel_app.utils

import android.content.Context
import android.content.SharedPreferences
import org.tumba.kegel_app.core.system.ResourceProviderImpl
import org.tumba.kegel_app.core.system.VibrationManagerImpl
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.domain.ExerciseServiceInteractor
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.service.ExerciseService
import org.tumba.kegel_app.service.ExerciseServiceProxy
import org.tumba.kegel_app.ui.exercise.ExerciseViewModelFactory
import org.tumba.kegel_app.ui.home.HomeViewModelFactory


object InjectorUtils {
    private const val APP_PREFERENCES_NAME = "APP_PREFERENCES_NAME"

    private val exerciseRepository by lazy { ExerciseRepository() }

    fun provideExerciseViewModelFactory(
        context: Context
    ): ExerciseViewModelFactory {
        val preferences = provideSharedPreferences(context.applicationContext)
        return ExerciseViewModelFactory(
            provideExerciseInteractor(context),
            provideExerciseServiceInteractor(context),
            ExerciseSettingsRepository(preferences),
            ResourceProviderImpl(context.resources)
        )
    }

    fun provideHomeViewModelFactory(
        context: Context
    ): HomeViewModelFactory {
        val preferences = provideSharedPreferences(context)
        return HomeViewModelFactory(
            ExerciseSettingsRepository(preferences)
        )
    }

    fun provideExerciseInteractor(context: Context): ExerciseInteractor {
        val preferences = provideSharedPreferences(context)
        return ExerciseInteractor(
            provideExerciseRepository(),
            ExerciseSettingsRepository(preferences),
            VibrationManagerImpl(context)
        )
    }

    private fun provideExerciseServiceInteractor(context: Context): ExerciseServiceInteractor {
        return ExerciseServiceInteractor(
            provideExerciseRepository(),
            provideExerciseService(context)
        )
    }

    private fun provideExerciseService(context: Context): ExerciseService {
        return ExerciseServiceProxy(context.applicationContext)
    }

    private fun provideExerciseRepository(): ExerciseRepository {
        return exerciseRepository
    }

    private fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}

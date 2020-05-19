package org.tumba.kegel_app.home

import io.reactivex.Completable
import io.reactivex.Observable
import org.tumba.kegel_app.data.ExerciseSettingsRepository
import javax.inject.Inject

class ExerciseSettingsInteractor @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun setVibrationEnabled(enabled: Boolean): Completable {
        return exerciseSettingsRepository.setVibrationEnabled(enabled)
    }

    fun isVibrationEnabled(): Observable<Boolean> {
        return exerciseSettingsRepository.isVibrationEnabled()
    }

    fun getExerciseLevel(): Observable<Int> {
        return exerciseSettingsRepository.getExerciseLevel()
    }

    fun incrementExerciseLevel(): Completable {
        return getExerciseLevel()
            .firstElement()
            .flatMapCompletable { level ->
                setExerciseLevel(level + 1)
            }
    }

    fun setExerciseDay(day: Int): Completable {
        return exerciseSettingsRepository.setExerciseDay(day)
    }

    fun getExerciseDay(): Observable<Int> {
        return exerciseSettingsRepository.getExerciseDay()
    }

    private fun setExerciseLevel(level: Int): Completable {
        return exerciseSettingsRepository.setExerciseLevel(level)
    }
}
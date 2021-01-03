package org.tumba.kegel_app.domain

import kotlinx.coroutines.flow.first
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class ExerciseFinishHandler @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseParametersProvider: ExerciseParametersProvider
) {

    suspend fun onExerciseStateChanged(state: ExerciseState) {
        if (state is ExerciseState.Finish && !state.isForceFinished) {
            updateLevelAndNumberOfExercises()
            updateExercisesDuration(state)
        }
    }

    private suspend fun updateLevelAndNumberOfExercises() {
        val day = exerciseParametersProvider.observeDay().first()
        updateLastCompletedExerciseDate(day)
        exerciseSettingsRepository.numberOfCompletedExercises.value++
        if (day % DAYS_IN_WEEK == 0) {
            exerciseSettingsRepository.exerciseLevel.value++
        }
    }

    private fun updateExercisesDuration(state: ExerciseState.Finish) {
        exerciseSettingsRepository.exercisesDurationInSeconds.value += state.exerciseInfo.durationSeconds
    }

    private fun updateLastCompletedExerciseDate(day: Int) {
        exerciseSettingsRepository.exerciseDay.value = day
        exerciseSettingsRepository.lastCompletedExerciseDate.value = System.currentTimeMillis()
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}
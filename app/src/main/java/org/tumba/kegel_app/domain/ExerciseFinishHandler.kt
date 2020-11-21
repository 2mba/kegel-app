package org.tumba.kegel_app.domain

import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class ExerciseFinishHandler @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun onExerciseStateChanged(state: ExerciseState) {
        if (state is ExerciseState.Finish && !state.isForceFinished) {
            updateLevelAndNumberOfExercises()
        }
    }

    private fun updateLevelAndNumberOfExercises() {
        val exercises = exerciseSettingsRepository.getNumberOfCompletedExercises()
        val level = exerciseSettingsRepository.getExerciseLevel()
        exerciseSettingsRepository.setNumberOfCompletedExercises(exercises + 1)
        if (exercises % DAYS_IN_WEEK == 1) {
            exerciseSettingsRepository.setExerciseLevel(level + 1)
        }
    }

    companion object {
        private const val DAYS_IN_WEEK = 2
    }
}
package org.tumba.kegel_app.domain

import kotlinx.coroutines.flow.first
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class ExerciseFinishHandler @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseParametersProvider: ExerciseParametersProvider
) {

    private var onFinishListeners = mutableListOf<() -> Unit>()

    suspend fun onExerciseStateChanged(state: ExerciseState) {
        if (state is ExerciseState.Finish) {
            onFinishListeners.forEach { it.invoke() }
            onFinishListeners.clear()
            if (!state.isForceFinished) {
                updateLevelAndNumberOfExercises(state)
                updateExercisesDuration(state)
            }
        }
    }

    fun onFinish(block: () -> Unit) {
        onFinishListeners.add(block)
    }

    private suspend fun updateLevelAndNumberOfExercises(state: ExerciseState.Finish) {
        val day = exerciseParametersProvider.observeDay().first()
        updateLastCompletedExerciseDate(day, state.exerciseInfo.isPredefined)
        exerciseSettingsRepository.numberOfCompletedExercises.value++
        if (state.exerciseInfo.isPredefined) {
            if (day % DAYS_IN_WEEK == 0) {
                exerciseSettingsRepository.exerciseLevel.value++
            }
        }
    }

    private fun updateExercisesDuration(state: ExerciseState.Finish) {
        exerciseSettingsRepository.exercisesDurationInSeconds.value += state.exerciseInfo.durationSeconds
    }

    private fun updateLastCompletedExerciseDate(day: Int, isPredefinedExercise: Boolean) {
        if (isPredefinedExercise) {
            exerciseSettingsRepository.exerciseDay.value = day
            exerciseSettingsRepository.lastCompletedPredefinedExerciseDate.value = System.currentTimeMillis()
        } else {
            exerciseSettingsRepository.lastCompletedCustomExerciseDate.value = System.currentTimeMillis()
        }
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}
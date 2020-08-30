package org.tumba.kegel_app.domain

import kotlinx.coroutines.flow.Flow
import org.tumba.kegel_app.core.system.IVibrationManager
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository

class ExerciseInteractor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val vibrationManager: IVibrationManager
) {

    suspend fun createExercise(config: ExerciseConfig) {
        exerciseRepository.saveExercise(createExerciseFrom(config))
    }

    suspend fun observeExerciseEvents(): Flow<ExerciseEvent> {
        return exerciseRepository.getExercise()?.observeEvents()
            ?: throw IllegalStateException("Exercise not found")
    }

    suspend fun startExercise() {
        exerciseRepository.getExercise()?.start()
    }

    suspend fun stopExercise() {
        exerciseRepository.getExercise()?.stop()
    }

    suspend fun pauseExercise() {
        exerciseRepository.getExercise()?.pause()
    }

    suspend fun resumeExercise() {
        exerciseRepository.getExercise()?.resume()
    }

    private fun createExerciseFrom(config: ExerciseConfig): Exercise {
        return Exercise(
            config = config,
            vibrationManager = vibrationManager,
            vibrationEnabledStateProvider = {
                exerciseSettingsRepository.isVibrationEnabled().value ?: true
            }
        )
    }
}
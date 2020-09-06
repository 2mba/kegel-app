package org.tumba.kegel_app.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository

@Suppress("EXPERIMENTAL_API_USAGE")
class ExerciseInteractor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val vibrationManager: VibrationManager
) {

    suspend fun getExercise(): Exercise? {
        return exerciseRepository.observeExercise().first()
    }

    suspend fun createExercise(config: ExerciseConfig) {
        exerciseRepository.saveExercise(createExerciseFrom(config))
    }

    fun observeExerciseState(): Flow<ExerciseState> {
        return exerciseRepository.observeExercise()
            .flatMapLatest { it?.observeState() ?: emptyFlow() }
            // ?: throw IllegalStateException("Exercise not found")
    }

    suspend fun isExerciseInProgress(): Boolean {
        return exerciseRepository.observeExercise().first().let { exercise ->
            exercise != null && exercise.observeState().first().isInProgress()
        }
    }

    suspend fun startExercise() {
        getExercise()?.start()
    }

    suspend fun stopExercise() {
        getExercise()?.stop()
    }

    suspend fun pauseExercise() {
        getExercise()?.pause()
    }

    suspend fun resumeExercise() {
        getExercise()?.resume()
    }

    suspend fun clearExercise() {
        exerciseRepository.deleteExercise()
    }

    private fun createExerciseFrom(config: ExerciseConfig): Exercise {
        return Exercise(
            config = config,
            vibrationManager = vibrationManager,
            vibrationEnabledStateProvider = {
                exerciseSettingsRepository.isVibrationEnabled()
            }
        )
    }

    private fun ExerciseState.isInProgress(): Boolean {
        val playingStates = listOf(
            ExerciseState.Preparation::class,
            ExerciseState.Relax::class,
            ExerciseState.Holding::class,
            ExerciseState.Pause::class
        )
        return playingStates.any { it == this::class  }
    }
}
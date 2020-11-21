package org.tumba.kegel_app.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.service.ExerciseService
import javax.inject.Inject

@Suppress("EXPERIMENTAL_API_USAGE")
class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val vibrationManager: VibrationManager,
    private val exerciseService: ExerciseService,
    private val exerciseProgram: ExerciseProgram,
    private val exerciseFinishHandler: ExerciseFinishHandler
) {

    suspend fun getExercise(): Exercise? {
        return exerciseRepository.observeExercise().first()
    }

    suspend fun createExercise() {
        exerciseRepository.saveExercise(
            createExerciseFrom(exerciseProgram.getConfig())
        )
    }

    fun observeExerciseState(): Flow<ExerciseState> {
        return exerciseRepository.observeExercise()
            .flatMapLatest { it?.observeState() ?: emptyFlow() }
    }

    suspend fun isExerciseInProgress(): Boolean {
        return exerciseRepository.observeExercise().first().let { exercise ->
            exercise != null && exercise.observeState().first().isInProgress()
        }
    }

    suspend fun startExercise() {
        if (exerciseSettingsRepository.isNotificationEnabled()) {
            exerciseService.startService()
        }
        val exercise = getExercise()
        if (exercise != null) {
            with(CoroutineScope(GlobalScope.coroutineContext + Dispatchers.IO)) {
                launch {
                    exercise.observeState().collect { exerciseFinishHandler.onExerciseStateChanged(it) }
                }
            }
            exercise.start()
        }
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

    suspend fun setNotificationEnabled(enabled: Boolean) {
        if (exerciseSettingsRepository.isNotificationEnabled() == enabled) return

        exerciseSettingsRepository.setNotificationEnabled(enabled)
        if (enabled && isExerciseInProgress()) {
            exerciseService.startService()
        } else {
            exerciseService.stopService()
        }
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
        return playingStates.any { it == this::class }
    }
}
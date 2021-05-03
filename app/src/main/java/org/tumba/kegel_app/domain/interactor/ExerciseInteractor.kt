package org.tumba.kegel_app.domain.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.tumba.kegel_app.domain.*
import org.tumba.kegel_app.floatingview.FloatingViewManager
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.service.ExerciseService
import javax.inject.Inject

@Suppress("EXPERIMENTAL_API_USAGE")
class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseService: ExerciseService,
    private val exerciseProgram: ExerciseProgram,
    private val exerciseEffectsHandler: ExerciseEffectsHandler,
    private val exerciseFinishHandler: ExerciseFinishHandler,
    private val floatingViewManager: FloatingViewManager
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
        if (exerciseSettingsRepository.isNotificationEnabled.value) {
            exerciseService.startService()
        }
        val exercise = getExercise()
        if (exercise != null) {
            with(CoroutineScope(GlobalScope.coroutineContext)) {
                launch(Dispatchers.Default) {
                    exercise.observeState().collect { exerciseFinishHandler.onExerciseStateChanged(it) }
                }
                launch(Dispatchers.Main) {
                    exercise.observeState().collect { floatingViewManager.updateFloatingViewState(it) }
                }
                exerciseFinishHandler.onFinish {
                    launch(Dispatchers.Main) { floatingViewManager.hideFloatingView() }
                }
            }
            exerciseEffectsHandler.onStartExercise(exercise.observeState())
            floatingViewManager.showFloatingView()
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
        if (exerciseSettingsRepository.isNotificationEnabled.value == enabled) return

        exerciseSettingsRepository.isNotificationEnabled.value = enabled
        if (enabled && isExerciseInProgress()) {
            exerciseService.startService()
        } else {
            exerciseService.stopService()
        }
    }

    fun isThereCompletedExercise(): Boolean {
        return exerciseSettingsRepository.lastCompletedExerciseDate.value != 0L
    }

    fun isFirstExerciseChallengeShown(): Boolean {
        return exerciseSettingsRepository.isFirstExerciseChallengeShown.value
    }

    suspend fun setFirstExerciseChallengeShown() {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.isFirstExerciseChallengeShown.value = true
        }
    }

    private fun createExerciseFrom(config: ExerciseConfig) = Exercise(config)

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
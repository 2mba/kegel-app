package org.tumba.kegel_app.domain.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.domain.*
import org.tumba.kegel_app.floatingview.FloatingViewManager
import org.tumba.kegel_app.repository.ExerciseRepository
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.service.ExerciseService
import org.tumba.kegel_app.ui.exercise.ExerciseBackgroundMode
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("EXPERIMENTAL_API_USAGE")
class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val proUpgradeManager: ProUpgradeManager,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseService: ExerciseService,
    private val exerciseProgram: ExerciseProgram,
    private val exerciseEffectsHandler: ExerciseEffectsHandler,
    private val exerciseFinishHandler: ExerciseFinishHandler,
    private val floatingViewManager: FloatingViewManager,
    private val customExerciseInteractor: CustomExerciseInteractor
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun getExercise(): Exercise? {
        return exerciseRepository.observeExercise().first()
    }

    suspend fun createPredefinedExercise() {
        exerciseRepository.saveExercise(
            createExerciseFrom(exerciseProgram.getConfig())
        )
    }

    suspend fun createCustomExercise() {
        val tenseDuration = customExerciseInteractor.observeTenseDuration().first()
        val relaxDuration = customExerciseInteractor.observeRelaxDuration().first()
        exerciseRepository.saveExercise(
            createExerciseFrom(
                ExerciseConfig(
                    preparationDuration = Time(ExerciseProgram.PREPARATION_TIME_SECONDS, TimeUnit.SECONDS),
                    holdingDuration = Time(tenseDuration.toLong(), TimeUnit.SECONDS),
                    relaxDuration = Time(relaxDuration.toLong(), TimeUnit.SECONDS),
                    repeats = customExerciseInteractor.observeRepeats().first(),
                    isPredefined = false
                )
            )
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
            observeExerciseFinish(exercise)
            scope.launch(Dispatchers.Main) {
                exercise.observeState()
                    .transformWhile { state ->
                        emit(state)
                        state !is ExerciseState.Finish
                    }
                    .collect { floatingViewManager.updateFloatingViewState(it) }
            }
            exerciseEffectsHandler.onStartExercise(exercise.observeState())
            if (observeBackgroundMode().first() == ExerciseBackgroundMode.FLOATING_VIEW) {
                floatingViewManager.showFloatingView()
            }
            exercise.start()
        }
    }

    private fun observeExerciseFinish(exercise: Exercise) {
        scope.launch(Dispatchers.Default + SupervisorJob()) {
            exercise.observeState()
                .transformWhile { state ->
                    emit(state)
                    state !is ExerciseState.Finish
                }
                .collect { exerciseFinishHandler.onExerciseStateChanged(it) }
        }
        exerciseFinishHandler.onFinish {
            scope.launch(Dispatchers.Main) { floatingViewManager.hideFloatingView() }
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

    fun isThereCompletedExercise(): Boolean {
        return exerciseSettingsRepository.lastCompletedPredefinedExerciseDate.value != 0L ||
                exerciseSettingsRepository.lastCompletedCustomExerciseDate.value != 0L
    }

    fun isFirstExerciseChallengeShown(): Boolean {
        return exerciseSettingsRepository.isFirstExerciseChallengeShown.value
    }

    suspend fun setFirstExerciseChallengeShown() {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.isFirstExerciseChallengeShown.value = true
        }
    }

    fun observeBackgroundMode(): Flow<ExerciseBackgroundMode> {
        val backgroundModeFlow = exerciseSettingsRepository.backgroundMode
            .asFlow()
            .map { ExerciseBackgroundMode.values().getOrElse(it) { ExerciseBackgroundMode.NONE } }
        return combine(
            proUpgradeManager.isProAvailable,
            backgroundModeFlow
        ) { isProAvailable, backgroundMode ->
            when {
                isProAvailable -> {
                    backgroundMode
                }
                backgroundMode == ExerciseBackgroundMode.FLOATING_VIEW -> {
                    exerciseSettingsRepository.backgroundMode.value = ExerciseBackgroundMode.NONE.ordinal
                    ExerciseBackgroundMode.NONE
                }
                else -> {
                    backgroundMode
                }
            }
        }
    }

    suspend fun setBackgroundMode(backgroundMode: ExerciseBackgroundMode) {
        if (!isExerciseInProgress()) {
            return
        }
        exerciseSettingsRepository.backgroundMode.value = backgroundMode.ordinal
        when (backgroundMode) {
            ExerciseBackgroundMode.NONE -> {
                exerciseService.stopService()
                floatingViewManager.hideFloatingView()
            }
            ExerciseBackgroundMode.NOTIFICATION -> {
                exerciseService.startService()
                floatingViewManager.hideFloatingView()
            }
            ExerciseBackgroundMode.FLOATING_VIEW -> {
                exerciseService.startService()
                floatingViewManager.showFloatingView()
            }
        }
    }

    fun isUserAgreementConfirmed(): Boolean {
        return exerciseSettingsRepository.isUserAgreementConfirmed.value
    }

    suspend fun setUserAgreementConfirmed() {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.isUserAgreementConfirmed.value = true
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
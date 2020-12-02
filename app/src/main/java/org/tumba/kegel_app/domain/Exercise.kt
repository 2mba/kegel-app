package org.tumba.kegel_app.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.core.system.VibrationManager.Strength
import org.tumba.kegel_app.domain.ExerciseStateInternal.CurrentState
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

@Suppress("EXPERIMENTAL_API_USAGE")
class Exercise(
    private val config: ExerciseConfig,
    private val vibrationManager: VibrationManager,
    private val vibrationEnabledStateProvider: () -> Boolean
) : CoroutineScope {

    override val coroutineContext = Dispatchers.Default + SupervisorJob()

    private var tickJob: Job? = null
    private val eventsChannel = BroadcastChannel<ExerciseState>(CONFLATED)
    private var exerciseState: ExerciseStateInternal = ExerciseStateInternal.NotStated

    fun observeState(): Flow<ExerciseState> = eventsChannel.asFlow()

    fun start() {
        check(exerciseState == ExerciseStateInternal.NotStated) { "Exercise should not be started" }
        startTickUpdates()
    }

    fun resume() {
        val state = exerciseState
        if (state is ExerciseStateInternal.Pause) {
            this.exerciseState = state.pausedState
            notifyState()
            startTickUpdates()
        }
    }

    fun pause() {
        val state = exerciseState
        if (state is ExerciseStateInternal.InProgress) {
            stopTickUpdates()
            this.exerciseState = ExerciseStateInternal.Pause(state)
            notifyState()
        }
    }

    fun stop() {
        stopTickUpdates()
        exerciseState = ExerciseStateInternal.Finish(isForceFinished = true)
        notifyState()
    }

    private fun tick() {
        when (exerciseState) {
            ExerciseStateInternal.NotStated -> tickNone()
            is ExerciseStateInternal.InProgress -> tickInProgress()
        }
    }

    private fun tickNone() {
        this.exerciseState = ExerciseStateInternal.InProgress(
            currentState = CurrentState.PREPARATION,
            repeatRemain = config.repeats - 1,
            startTime = System.currentTimeMillis()
        )
        vibrate(Strength.Low)
        notifyState()
    }

    private fun tickInProgress() {
        val state = exerciseState as? ExerciseStateInternal.InProgress ?: return
        val timePassed = System.currentTimeMillis() - state.startTime
        val remainSeconds = ((getExerciseDuration(state) - timePassed / 1000.0).roundToLong())
        when (state.currentState) {
            CurrentState.PREPARATION -> {
                tickPreparation(remainSeconds)
            }
            CurrentState.HOLDING -> {
                tickHolding(remainSeconds, state)
            }
            CurrentState.RELAX -> {
                tickRelax(remainSeconds, state)
            }
        }
    }

    private fun tickPreparation(remainSeconds: Long) {
        if (remainSeconds <= 0) {
            this.exerciseState = ExerciseStateInternal.InProgress(
                currentState = CurrentState.HOLDING,
                repeatRemain = config.repeats - 1,
                startTime = System.currentTimeMillis()
            )
        }
        notifyState()
    }

    private fun tickHolding(
        remainSeconds: Long,
        exerciseState: ExerciseStateInternal.InProgress
    ) {
        if (remainSeconds <= 0) {
            this.exerciseState = ExerciseStateInternal.InProgress(
                currentState = CurrentState.RELAX,
                repeatRemain = exerciseState.repeatRemain,
                startTime = System.currentTimeMillis()
            )
            vibrate(Strength.Medium)
        }
        notifyState()
    }

    private fun tickRelax(
        remainSeconds: Long,
        exerciseState: ExerciseStateInternal.InProgress
    ) {
        if (remainSeconds <= 0) {
            if (exerciseState.repeatRemain == 0) {
                finishExercise()
            } else {
                this.exerciseState =
                    ExerciseStateInternal.InProgress(
                        currentState = CurrentState.HOLDING,
                        repeatRemain = exerciseState.repeatRemain - 1,
                        startTime = System.currentTimeMillis()
                    )
                vibrate(Strength.Medium)
            }
        }
        notifyState()
    }

    private fun finishExercise() {
        tickJob?.cancel()
        exerciseState = ExerciseStateInternal.Finish(isForceFinished = false)
    }

    private fun notifyState() {
        launch {
            when (val state = exerciseState) {
                ExerciseStateInternal.NotStated -> {
                    eventsChannel.send(ExerciseState.NotStarted)
                }
                is ExerciseStateInternal.InProgress -> notifyInProgressState(state)
                is ExerciseStateInternal.Pause -> {
                    val remainSeconds = getExerciseRemainTimeSeconds(state.pausedState)
                    eventsChannel.send(
                        ExerciseState.Pause(
                            singleExerciseInfo = SingleExerciseInfo(
                                remainSeconds = remainSeconds,
                                exerciseDurationSeconds = getExerciseDuration(state.pausedState)
                            ),
                            exerciseInfo = getExerciseInfo(state.pausedState)
                        )
                    )
                }
                is ExerciseStateInternal.Finish -> {
                    eventsChannel.send(ExerciseState.Finish(state.isForceFinished))
                }
            }
        }
    }

    private suspend fun notifyInProgressState(state: ExerciseStateInternal.InProgress) {
        val remainSeconds = getExerciseRemainTimeSeconds(state)
        val singleExerciseInfo = SingleExerciseInfo(
            remainSeconds = remainSeconds,
            exerciseDurationSeconds = getExerciseDuration(state)
        )
        when (state.currentState) {
            CurrentState.PREPARATION -> {
                eventsChannel.send(
                    ExerciseState.Preparation(singleExerciseInfo, getExerciseInfo(state))
                )
            }
            CurrentState.HOLDING -> {
                eventsChannel.send(
                    ExerciseState.Holding(singleExerciseInfo, getExerciseInfo(state))
                )
            }
            CurrentState.RELAX -> {
                eventsChannel.send(
                    ExerciseState.Relax(singleExerciseInfo, getExerciseInfo(state))
                )
            }
        }
    }

    private fun getExerciseInfo(state: ExerciseStateInternal.InProgress): ExerciseInfo {
        return ExerciseInfo(
            getExerciseRemainSeconds(state),
            repeatRemains = state.repeatRemain
        )
    }

    private fun getExerciseRemainSeconds(state: ExerciseStateInternal.InProgress): Long {
        val repeatsRemain = state.repeatRemain
        val repeatDuration = config.holdingDuration.toSeconds() + config.relaxDuration.toSeconds()
        val remainRepeatsDuration = repeatsRemain * repeatDuration
        val currentRepeatDuration = when (state.currentState) {
            CurrentState.PREPARATION -> repeatDuration
            CurrentState.HOLDING -> getExerciseRemainTimeSeconds(state) + config.relaxDuration.toSeconds()
            CurrentState.RELAX -> getExerciseRemainTimeSeconds(state)
        }
        return remainRepeatsDuration + currentRepeatDuration
    }

    private fun getExerciseDuration(state: ExerciseStateInternal.InProgress): Long {
        return when (state.currentState) {
            CurrentState.PREPARATION -> config.preparationDuration.toSeconds()
            CurrentState.HOLDING -> config.holdingDuration.toSeconds()
            CurrentState.RELAX -> config.relaxDuration.toSeconds()
        }
    }

    private fun getExerciseRemainTimeSeconds(state: ExerciseStateInternal.InProgress): Long {
        val timePassed = System.currentTimeMillis() - state.startTime
        return (getExerciseDuration(state) - timePassed / 1000)
    }

    private fun vibrate(strength: Strength) {
        val isVibrationEnabled = vibrationEnabledStateProvider()
        if (isVibrationEnabled) {
            vibrationManager.vibrate(strength)
        }
    }

    private fun startTickUpdates() {
        tickJob = launch {
            while (true) {
                tick()
                delay(UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    private fun stopTickUpdates() {
        tickJob?.cancel()
    }

    companion object {
        private const val UPDATE_INTERVAL_MILLIS = 1000L
    }
}

private sealed class ExerciseStateInternal {

    object NotStated : ExerciseStateInternal()

    data class InProgress(
        val currentState: CurrentState,
        val repeatRemain: Int,
        val startTime: Long
    ) : ExerciseStateInternal()

    data class Pause(
        val pausedState: InProgress
    ) : ExerciseStateInternal()

    class Finish(val isForceFinished: Boolean) : ExerciseStateInternal()

    enum class CurrentState {
        PREPARATION,
        HOLDING,
        RELAX
    }
}

data class Time(
    val quantity: Long,
    val unit: TimeUnit
)

fun Time.toMillis(): Long = unit.toMillis(quantity)

fun Time.toSeconds(): Long = unit.toSeconds(quantity)

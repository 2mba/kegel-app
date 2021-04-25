package org.tumba.kegel_app.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.core.system.VibrationManager.Strength
import org.tumba.kegel_app.domain.ExerciseStateInternal.ExerciseKind
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@Suppress("EXPERIMENTAL_API_USAGE")
@OptIn(ExperimentalTime::class)
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
            this.exerciseState = state.pausedState.copy(
                timeForSkip = state.pausedState.timeForSkip + state.pausedAt.elapsedNow()
            )
            notifyState()
            startTickUpdates()
        }
    }

    fun pause() {
        val state = exerciseState
        if (state is ExerciseStateInternal.InProgress) {
            stopTickUpdates()
            this.exerciseState = ExerciseStateInternal.Pause(pausedState = state, pausedAt = now())
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
            else -> {
            }
        }
    }

    private fun tickNone() {
        exerciseState = ExerciseStateInternal.InProgress(
            exerciseKind = ExerciseKind.PREPARATION,
            repeatRemain = config.repeats,
            startedAt = now(),
            timeForSkip = Duration.ZERO
        )
        vibrate(Strength.Low)
        notifyState()
    }

    private fun tickInProgress() {
        val state = exerciseState as? ExerciseStateInternal.InProgress ?: return
        val timePassedSeconds = (state.startedAt.elapsedNow() - state.timeForSkip).inSeconds
        val remainSeconds = ((getSingleExerciseDuration(state) - timePassedSeconds).roundToLong())
        when (state.exerciseKind) {
            ExerciseKind.PREPARATION -> tickPreparation(remainSeconds)
            ExerciseKind.HOLDING -> tickHolding(remainSeconds, state)
            ExerciseKind.RELAX -> tickRelax(remainSeconds, state)
        }
    }

    private fun tickPreparation(remainSeconds: Long) {
        if (remainSeconds <= 0) {
            this.exerciseState = ExerciseStateInternal.InProgress(
                exerciseKind = ExerciseKind.HOLDING,
                repeatRemain = config.repeats,
                startedAt = now(),
                timeForSkip = Duration.ZERO
            )
            vibrate(Strength.Medium)
        }
        notifyState()
    }

    private fun tickHolding(
        remainSeconds: Long,
        exerciseState: ExerciseStateInternal.InProgress
    ) {
        if (remainSeconds <= 0) {
            this.exerciseState = ExerciseStateInternal.InProgress(
                exerciseKind = ExerciseKind.RELAX,
                repeatRemain = exerciseState.repeatRemain,
                startedAt = now(),
                timeForSkip = Duration.ZERO
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
            if (exerciseState.repeatRemain <= 1) {
                finishExercise()
            } else {
                this.exerciseState =
                    ExerciseStateInternal.InProgress(
                        exerciseKind = ExerciseKind.HOLDING,
                        repeatRemain = exerciseState.repeatRemain - 1,
                        startedAt = now(),
                        timeForSkip = Duration.ZERO
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
                                exerciseDurationSeconds = getSingleExerciseDuration(state.pausedState)
                            ),
                            exerciseInfo = getExerciseInfo(state.pausedState)
                        )
                    )
                }
                is ExerciseStateInternal.Finish -> {
                    val exerciseInfo = ExerciseInfo(
                        remainSeconds = 0,
                        repeatRemains = 0,
                        durationSeconds = getFullExerciseDuration(),
                        repeats = config.repeats
                    )
                    eventsChannel.send(ExerciseState.Finish(state.isForceFinished, exerciseInfo))
                }
            }
        }
    }

    private suspend fun notifyInProgressState(state: ExerciseStateInternal.InProgress) {
        val remainSeconds = getExerciseRemainTimeSeconds(state)
        val singleExerciseInfo = SingleExerciseInfo(
            remainSeconds = remainSeconds,
            exerciseDurationSeconds = getSingleExerciseDuration(state)
        )
        when (state.exerciseKind) {
            ExerciseKind.PREPARATION -> {
                eventsChannel.send(
                    ExerciseState.Preparation(singleExerciseInfo, getExerciseInfo(state))
                )
            }
            ExerciseKind.HOLDING -> {
                eventsChannel.send(
                    ExerciseState.Holding(singleExerciseInfo, getExerciseInfo(state))
                )
            }
            ExerciseKind.RELAX -> {
                eventsChannel.send(
                    ExerciseState.Relax(singleExerciseInfo, getExerciseInfo(state))
                )
            }
        }
    }

    private fun getExerciseInfo(state: ExerciseStateInternal.InProgress): ExerciseInfo {
        return ExerciseInfo(
            remainSeconds = getExerciseRemainSeconds(state),
            repeatRemains = state.repeatRemain,
            durationSeconds = getFullExerciseDuration(),
            repeats = config.repeats
        )
    }

    private fun getExerciseRemainSeconds(state: ExerciseStateInternal.InProgress): Long {
        val repeatDuration = config.holdingDuration.toSeconds() + config.relaxDuration.toSeconds()
        val remainRepeatsDuration = (state.repeatRemain - 1) * repeatDuration
        val currentRepeatDuration = when (state.exerciseKind) {
            ExerciseKind.PREPARATION -> repeatDuration
            ExerciseKind.HOLDING -> getExerciseRemainTimeSeconds(state) + config.relaxDuration.toSeconds()
            ExerciseKind.RELAX -> getExerciseRemainTimeSeconds(state)
        }
        return remainRepeatsDuration + currentRepeatDuration
    }

    private fun getFullExerciseDuration(): Long {
        return (config.holdingDuration.toSeconds() + config.relaxDuration.toSeconds()) * config.repeats
    }

    private fun getSingleExerciseDuration(state: ExerciseStateInternal.InProgress): Long {
        return when (state.exerciseKind) {
            ExerciseKind.PREPARATION -> config.preparationDuration.toSeconds()
            ExerciseKind.HOLDING -> config.holdingDuration.toSeconds()
            ExerciseKind.RELAX -> config.relaxDuration.toSeconds()
        }
    }

    private fun getExerciseRemainTimeSeconds(state: ExerciseStateInternal.InProgress): Long {
        val timePassed = (state.startedAt.elapsedNow() - state.timeForSkip).inSeconds.toInt()
        return (getSingleExerciseDuration(state) - timePassed)
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

    private fun now() = TimeSource.Monotonic.markNow()

    companion object {
        private const val UPDATE_INTERVAL_MILLIS = 1000L
    }
}

@OptIn(ExperimentalTime::class)
private sealed class ExerciseStateInternal {

    object NotStated : ExerciseStateInternal()

    data class InProgress constructor(
        val exerciseKind: ExerciseKind,
        val repeatRemain: Int,
        val startedAt: TimeMark,
        val timeForSkip: Duration
    ) : ExerciseStateInternal()

    data class Pause(
        val pausedState: InProgress,
        val pausedAt: TimeMark
    ) : ExerciseStateInternal()

    class Finish(val isForceFinished: Boolean) : ExerciseStateInternal()

    enum class ExerciseKind {
        PREPARATION,
        HOLDING,
        RELAX
    }
}

data class Time(
    val quantity: Long,
    val unit: TimeUnit
)

fun Time.toSeconds(): Long = unit.toSeconds(quantity)

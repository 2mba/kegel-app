package org.tumba.kegel_app.data

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.tumba.kegel_app.core.system.IVibrationManager
import org.tumba.kegel_app.core.system.IVibrationManager.Strength
import org.tumba.kegel_app.data.State.CurrentState
import java.util.concurrent.TimeUnit

class Exercise(
    private val config: ExerciseConfig,
    private val vibrationManager: IVibrationManager,
    private val vibrationEnabledStateProvider: () -> Boolean
) {

    companion object {
        private const val UPDATE_INTERVAL_SECONDS = 1L
        private const val UPDATE_INITIAL_DELAY_SECONDS = 0L
    }

    val events: Observable<ExerciseEvent>
        get() = eventsSubject
    private val eventsSubject = PublishSubject.create<ExerciseEvent>()
    private var state: State =
        State.None
    private var intervalDisposable: Disposable? = null

    fun start() {
        check(state == State.None) { "Exercise should not be started" }
        startTickUpdates()
    }

    fun resume() {
        val state = state
        if (state is State.Pause) {
            this.state = state.pausedState
            notifyState()
            startTickUpdates()
        }
    }

    fun pause() {
        val state = state
        if (state is State.InProgress) {
            stopTickUpdates()
            this.state = State.Pause(state)
            notifyState()
        }
    }

    fun stop() {
        stopTickUpdates()
        state = State.None
        notifyState()
    }

    private fun tick() {
        when (state) {
            State.None -> tickNone()
            is State.InProgress -> tickInProgress()
        }
    }

    private fun tickNone() {
        this.state = State.InProgress(
            currentState = CurrentState.PREPARATION,
            repeatRemain = config.repeats - 1,
            startTime = System.currentTimeMillis()
        )
        vibrate(Strength.Low)
        notifyState()
    }

    private fun tickInProgress() {
        val state = state as? State.InProgress ?: return
        val timePassed = System.currentTimeMillis() - state.startTime
        val remainSeconds = (getDurationState(state) - timePassed + 1) / 1000
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
            this.state = State.InProgress(
                currentState = CurrentState.HOLDING,
                repeatRemain = config.repeats - 1,
                startTime = System.currentTimeMillis()
            )
        }
        notifyState()
    }

    private fun tickHolding(
        remainSeconds: Long,
        state: State.InProgress
    ) {
        if (remainSeconds <= 0) {
            this.state = State.InProgress(
                currentState = CurrentState.RELAX,
                repeatRemain = state.repeatRemain,
                startTime = System.currentTimeMillis()
            )
            vibrate(Strength.Medium)
        }
        notifyState()
    }

    private fun tickRelax(
        remainSeconds: Long,
        state: State.InProgress
    ) {
        if (remainSeconds <= 0) {
            if (state.repeatRemain == 0) {
                finishExercise()
            } else {
                this.state = State.InProgress(
                    currentState = CurrentState.HOLDING,
                    repeatRemain = state.repeatRemain - 1,
                    startTime = System.currentTimeMillis()
                )
                vibrate(Strength.Medium)
            }
        }
        notifyState()
    }

    private fun finishExercise() {
        intervalDisposable?.dispose()
        eventsSubject.onNext(ExerciseEvent.Finish)
        state = State.None
    }

    private fun notifyState() {
        val state = (state as? State.InProgress) ?: return
        val timePassed = System.currentTimeMillis() - state.startTime
        val remainSeconds = (getDurationState(state) - timePassed + 1) / 1000
        when (state.currentState) {
            CurrentState.PREPARATION -> {
                eventsSubject.onNext(
                    ExerciseEvent.Preparation(
                        remainSeconds = remainSeconds,
                        exerciseDurationSeconds = config.preparationDuration.toSeconds()
                    )
                )
            }
            CurrentState.HOLDING -> {
                eventsSubject.onNext(
                    ExerciseEvent.Holding(
                        remainSeconds = remainSeconds,
                        repeatRemains = state.repeatRemain,
                        exerciseDurationSeconds = config.holdingDuration.toSeconds()
                    )
                )
            }
            CurrentState.RELAX -> {
                eventsSubject.onNext(
                    ExerciseEvent.Relax(
                        remainSeconds = remainSeconds,
                        repeatsRemain = state.repeatRemain,
                        exerciseDurationSeconds = config.relaxDuration.toSeconds()
                    )
                )
            }
        }
    }

    private fun vibrate(strength: Strength) {
        val isVibrationEnabled = vibrationEnabledStateProvider()
        if (isVibrationEnabled) {
            vibrationManager.vibrate(strength)
        }
    }

    private fun getDurationState(state: State.InProgress): Long {
        return when (state.currentState) {
            CurrentState.PREPARATION -> config.preparationDuration.toMillis()
            CurrentState.HOLDING -> config.holdingDuration.toMillis()
            CurrentState.RELAX -> config.relaxDuration.toMillis()
        }
    }


    private fun startTickUpdates() {
        intervalDisposable = Observable.interval(
            UPDATE_INITIAL_DELAY_SECONDS,
            UPDATE_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        )
            .subscribe { tick() }
    }

    private fun stopTickUpdates() {
        intervalDisposable?.dispose()
    }
}

private sealed class State {

    object None : State()

    data class InProgress(
        val currentState: CurrentState,
        val repeatRemain: Int,
        val startTime: Long
    ) : State()

    data class Pause(
        val pausedState: InProgress
    ) : State()

    enum class CurrentState {
        PREPARATION,
        HOLDING,
        RELAX
    }
}

sealed class ExerciseEvent {

    data class Preparation(
        val remainSeconds: Long,
        val exerciseDurationSeconds: Long
    ) : ExerciseEvent()

    data class Holding(
        val remainSeconds: Long,
        val repeatRemains: Int,
        val exerciseDurationSeconds: Long
    ) : ExerciseEvent()

    data class Relax(
        val remainSeconds: Long,
        val repeatsRemain: Int,
        val exerciseDurationSeconds: Long
    ) : ExerciseEvent()

    data class Pause(val remainSeconds: Long, val repeatsRemain: Int) : ExerciseEvent()

    object Stop : ExerciseEvent()

    object Finish : ExerciseEvent()
}

data class Time(
    val quantity: Long,
    val unit: TimeUnit
)

fun Time.toMillis(): Long = unit.toMillis(quantity)

fun Time.toSeconds(): Long = unit.toSeconds(quantity)

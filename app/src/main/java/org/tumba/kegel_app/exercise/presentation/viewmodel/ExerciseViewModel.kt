package org.tumba.kegel_app.exercise.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.IResourceProvider
import org.tumba.kegel_app.exercise.core.presentation.CoreViewModel
import org.tumba.kegel_app.exercise.core.presentation.disposeOnDestroy
import org.tumba.kegel_app.exercise.domain.entity.ExerciseConfig
import org.tumba.kegel_app.exercise.domain.entity.ExerciseEvent
import org.tumba.kegel_app.exercise.domain.entity.Time
import org.tumba.kegel_app.exercise.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.exercise.presentation.model.ExerciseStateUiModel
import org.tumba.kegel_app.exercise.utils.Empty
import org.tumba.kegel_app.exercise.utils.async
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor,
    private val resourceProvider: IResourceProvider
) : CoreViewModel() {

    companion object {
        private const val TIMER_INTERVAL_MILLIS = 20L
        private const val MILLIS_IN_SECONDS = 1000L
    }

    val exerciseKind = MutableLiveData(String.Empty)
    val exerciseState = MutableLiveData(ExerciseStateUiModel.Playing)
    val repeatsRemain = MutableLiveData(0)
    val secondsRemain = MutableLiveData(0L)
    val exerciseProgress = MutableLiveData(0F)
    val level = MutableLiveData(5)
    val day = MutableLiveData(2)
    val isVibrationEnabled = MutableLiveData(true)

    private var exerciseDuration = 0L
    private var currentState: ExerciseEvent? = null
    private var timerDisposable: Disposable? = null
    private var isProgressReversed = true

    init {
        loadVibrationState()
        startExercise()
    }

    fun onClickPlay() {
        when (exerciseState.value) {
            ExerciseStateUiModel.Playing -> {
                exerciseState.value = ExerciseStateUiModel.Paused
            }
            ExerciseStateUiModel.Paused -> {
                exerciseState.value = ExerciseStateUiModel.Playing
            }
        }
    }

    fun onClickNotification() {

    }

    fun onClickVibration() {
        val isVibrationEnabled = isVibrationEnabled.value ?: true
        this.isVibrationEnabled.value = !isVibrationEnabled
        exerciseInteractor.setVibrationEnabled(!isVibrationEnabled)
            .async()
            .subscribe()
            .disposeOnDestroy(this)
    }

    private fun startExercise() {
        exerciseInteractor.createExercise(
            config = ExerciseConfig(
                preparationDuration = Time(3, TimeUnit.SECONDS),
                holdingDuration = Time(1, TimeUnit.SECONDS),
                relaxDuration = Time(1, TimeUnit.SECONDS),
                repeats = 5
            )
        )
            .andThen(exerciseInteractor.startExercise())
            .andThen(exerciseInteractor.subscribeToExerciseEvents())
            .async()
            .subscribeBy(
                onNext = ::onExerciseEventReceived
            )
            .disposeOnDestroy(this)
    }

    private fun onExerciseEventReceived(event: ExerciseEvent?) {
        when (event) {
            is ExerciseEvent.Preparation -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value =
                    resourceProvider.getString(R.string.screen_exercise_exercise_preparation)
                secondsRemain.value = event.remainSeconds
            }
            is ExerciseEvent.Holding -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value =
                    resourceProvider.getString(R.string.screen_exercise_exercise_holding)
                repeatsRemain.value = event.repeatRemains
                secondsRemain.value = event.remainSeconds
                isProgressReversed = true
                restartRemainTimer()
            }
            is ExerciseEvent.Relax -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value =
                    resourceProvider.getString(R.string.screen_exercise_exercise_relax)
                repeatsRemain.value = event.repeatsRemain
                secondsRemain.value = event.remainSeconds
                isProgressReversed = false
                restartRemainTimer()
            }
            is ExerciseEvent.Finish -> {
                stopRemainTimer()
            }
        }
        currentState = event
    }

    private fun restartRemainTimer() {
        val secondsRemain = secondsRemain.value ?: 0
        stopRemainTimer()
        timerDisposable = Observable.interval(
            TIMER_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            AndroidSchedulers.mainThread()
        )
            .subscribe { time ->
                val millisRemain =
                    secondsRemain.toFloat() * MILLIS_IN_SECONDS - time * TIMER_INTERVAL_MILLIS
                exerciseProgress.value = if (isProgressReversed) {
                    1 - millisRemain / (exerciseDuration * MILLIS_IN_SECONDS)
                } else {
                    millisRemain / (exerciseDuration * MILLIS_IN_SECONDS)
                }
            }
            .disposeOnDestroy(this)
    }

    private fun stopRemainTimer() {
        timerDisposable?.dispose()
    }

    private fun loadVibrationState() {
        exerciseInteractor.isVibrationEnabled()
            .async()
            .subscribeBy(
                onNext = { isVibrationEnabled.value = it }
            )
            .disposeOnDestroy(this)
    }
}
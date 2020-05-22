package org.tumba.kegel_app.ui.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.IResourceProvider
import org.tumba.kegel_app.domain.ExerciseConfig
import org.tumba.kegel_app.domain.ExerciseEvent
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.domain.Time
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.disposeOnDestroy
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.async
import java.util.concurrent.TimeUnit

class ExerciseViewModel(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val resourceProvider: IResourceProvider
) : BaseViewModel() {

    companion object {
        private const val TIMER_INTERVAL_MILLIS = 20L
        private const val MILLIS_IN_SECONDS = 1000L
    }

    val exerciseKind = MutableLiveData(String.Empty)
    val exerciseState = MutableLiveData(ExerciseStateUiModel.Playing)
    val repeatsRemain = MutableLiveData(0)
    val secondsRemain = MutableLiveData(0L)
    val minuteRemain = MutableLiveData(0L)
    val exerciseProgress = MutableLiveData(0F)
    val level = exerciseSettingsRepository.getExerciseLevel()
    val day = exerciseSettingsRepository.getExerciseDay()
    val isVibrationEnabled = exerciseSettingsRepository.isVibrationEnabled()

    private var exerciseDuration = 0L
    private var currentState: ExerciseEvent? = null
    private var timerDisposable: Disposable? = null
    private var isProgressReversed = true

    init {
        startExercise()
    }

    fun onClickPlay() {
        when (exerciseState.value) {
            ExerciseStateUiModel.Playing -> {
                stopRemainTimer()
                exerciseState.value =
                    ExerciseStateUiModel.Paused
                exerciseInteractor.pauseExercise()
                    .async()
                    .subscribe()
            }
            ExerciseStateUiModel.Paused -> {
                exerciseState.value =
                    ExerciseStateUiModel.Playing
                exerciseInteractor.resumeExercise()
                    .async()
                    .subscribe()
            }
        }
    }

    fun onClickNotification() {
    }

    fun onClickVibration() {
        val isVibrationEnabled = isVibrationEnabled.value ?: true
        viewModelScope.launch {
            exerciseSettingsRepository.setVibrationEnabled(!isVibrationEnabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopExercise()
    }

    private fun startExercise() {
        exerciseInteractor.createExercise(
            config = ExerciseConfig(
                preparationDuration = Time(
                    3,
                    TimeUnit.SECONDS
                ),
                holdingDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                relaxDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
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
                finishExercise()
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

    private fun stopExercise() {
        stopRemainTimer()
        exerciseInteractor.stopExercise()
            .async()
            .subscribe()
    }

    private fun finishExercise() {
        stopRemainTimer()
        viewModelScope.launch {
            exerciseSettingsRepository.setExerciseLevel((level.value ?: 0) + 1)
        }
    }
}
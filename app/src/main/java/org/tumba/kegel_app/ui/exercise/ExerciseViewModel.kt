package org.tumba.kegel_app.ui.exercise

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.IResourceProvider
import org.tumba.kegel_app.domain.ExerciseConfig
import org.tumba.kegel_app.domain.ExerciseEvent
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.domain.Time
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.utils.Empty
import java.util.concurrent.TimeUnit

class ExerciseViewModel(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val resourceProvider: IResourceProvider
) : BaseViewModel() {

    val exerciseKind = MutableLiveData(String.Empty)
    val exerciseState = MutableLiveData(ExerciseStateUiModel.Playing)
    val repeatsRemain = MutableLiveData(0)
    val timeRemain = MediatorLiveData<String>()
    val exerciseProgress = MutableLiveData(0F)
    val level = exerciseSettingsRepository.getExerciseLevel()
    val day = exerciseSettingsRepository.getExerciseDay()
    val isVibrationEnabled = exerciseSettingsRepository.isVibrationEnabled()
    private val secondsRemain = MutableLiveData(0L)
    private var exerciseDuration = 0L
    private var currentState: ExerciseEvent? = null
    private var isProgressReversed = true

    init {
        startExercise()
        timeRemain.apply {
            addSource(secondsRemain) { seconds -> value = formatTimeRemains(seconds) }
            value = formatTimeRemains(0)
        }
    }

    fun onClickPlay() {
        when (exerciseState.value) {
            ExerciseStateUiModel.Playing -> {
                exerciseState.value = ExerciseStateUiModel.Paused
                viewModelScope.launch {
                    exerciseInteractor.pauseExercise()
                }
            }
            ExerciseStateUiModel.Paused -> {
                exerciseState.value = ExerciseStateUiModel.Playing
                viewModelScope.launch {
                    exerciseInteractor.resumeExercise()
                }
            }
        }
    }

    fun onClickNotification() {
    }

    fun onVibrationStateChanged(enabled: Boolean) {
        viewModelScope.launch {
            exerciseSettingsRepository.setVibrationEnabled(enabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopExercise()
    }

    private fun startExercise() {
        viewModelScope.launch {
            exerciseInteractor.createExercise(
                config = ExerciseConfig(
                    preparationDuration = Time(3, TimeUnit.SECONDS),
                    holdingDuration = Time(5, TimeUnit.SECONDS),
                    relaxDuration = Time(5, TimeUnit.SECONDS),
                    repeats = 3
                )
            )
            exerciseInteractor.startExercise()
            exerciseInteractor.observeExerciseEvents()
                .collect { onExerciseEventReceived(it) }
        }
    }

    private fun onExerciseEventReceived(event: ExerciseEvent?) {
        Log.d("!!!!", "Event: $event")
        when (event) {
            is ExerciseEvent.Preparation -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value = resourceProvider.getString(R.string.screen_exercise_exercise_preparation)
                secondsRemain.value = event.remainSeconds
                isProgressReversed = false
                updateExerciseProgress()
            }
            is ExerciseEvent.Holding -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value = resourceProvider.getString(R.string.screen_exercise_exercise_holding)
                repeatsRemain.value = event.repeatRemains
                secondsRemain.value = event.remainSeconds
                isProgressReversed = true
                updateExerciseProgress()
            }
            is ExerciseEvent.Relax -> {
                exerciseDuration = event.exerciseDurationSeconds
                exerciseKind.value = resourceProvider.getString(R.string.screen_exercise_exercise_relax)
                repeatsRemain.value = event.repeatsRemain
                secondsRemain.value = event.remainSeconds
                isProgressReversed = false
                updateExerciseProgress()
            }
            is ExerciseEvent.Finish -> {
                finishExercise()
            }
        }
        currentState = event
    }

    private fun updateExerciseProgress() {
        val secondsRemain = secondsRemain.value ?: 0
        exerciseProgress.value = if (isProgressReversed) {
            1 - secondsRemain / (exerciseDuration - 1).toFloat()
        } else {
            secondsRemain / (exerciseDuration - 1).toFloat()
        }
    }

    private fun stopExercise() {
        viewModelScope.launch {
            exerciseInteractor.stopExercise()
        }
    }

    private fun finishExercise() {
        viewModelScope.launch {
            exerciseSettingsRepository.setExerciseLevel((level.value ?: 0) + 1)
        }
    }

    private fun formatTimeRemains(seconds: Long): String {
        val sec = seconds % 60
        val min = seconds / 60
        return String.format("%02d:%02d", min, sec)
    }
}
package org.tumba.kegel_app.ui.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.domain.ExerciseServiceInteractor
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.*
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.Event
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseServiceInteractor: ExerciseServiceInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseNameProvider: ExerciseNameProvider
) : BaseViewModel() {

    val exerciseKind = MutableLiveData(String.Empty)
    val exercisePlaybackState by lazy { _exercisePlaybackState.distinctUntilChanged() }
    val repeatsRemain = MutableLiveData(0)
    val timeRemain by lazy { secondsRemain.map { formatTimeRemains(it) } }
    val exerciseProgress = MutableLiveData(0F)
    val level = exerciseSettingsRepository.observeExerciseLevel()
    val day = exerciseSettingsRepository.getExerciseDay()
    val isVibrationEnabled = exerciseSettingsRepository.observeVibrationEnabled()
    val isNotificationEnabled = exerciseSettingsRepository.observeNotificationEnabled()
    val exitConfirmationDialogVisible = MutableLiveData(Event(false))
    val exit = MutableLiveData(Event(false))
    val navigateToExerciseResult = MutableLiveData(Event(false))
    private val _exercisePlaybackState = MutableLiveData(Playing)
    private val secondsRemain = MutableLiveData(0L)
    private var exerciseDuration = 0L
    private var currentState: ExerciseState? = null
    private var isProgressReversed = true
    private var isExerciseStoppedFromExerciseScreen = false

    init {
        startExerciseIfNoExerciseInProgress()
        observeExerciseState()
    }

    fun onClickPlay() {
        when (exercisePlaybackState.value) {
            Playing -> {
                viewModelScope.launch {
                    exerciseInteractor.pauseExercise()
                }
            }
            Paused -> {
                viewModelScope.launch {
                    exerciseInteractor.resumeExercise()
                }
            }
            else -> {
            }
        }
    }

    fun onClickStop() {
        viewModelScope.launch {
            isExerciseStoppedFromExerciseScreen = true
            exerciseInteractor.stopExercise()
        }
    }

    fun onNotificationStateChanged(enabled: Boolean) {
        viewModelScope.launch {
            exerciseInteractor.setNotificationEnabled(enabled)
        }
    }

    fun onVibrationStateChanged(enabled: Boolean) {
        viewModelScope.launch {
            exerciseSettingsRepository.setVibrationEnabled(enabled)
        }
    }

    fun onBackPressed() {
        if (_exercisePlaybackState.value == Playing) {
            viewModelScope.launch {
                exerciseInteractor.pauseExercise()
            }
        }
        if (_exercisePlaybackState.value == Stopped) {
            exit()
        } else {
            exitConfirmationDialogVisible.value = Event(true)
        }
    }

    fun onConfirmationDialogCanceled() {
    }

    fun onExitConfirmed() {
        viewModelScope.launch {
            isExerciseStoppedFromExerciseScreen = true
            exerciseInteractor.stopExercise()
        }
    }

    private fun startExerciseIfNoExerciseInProgress() {
        viewModelScope.launch {
            if (!exerciseInteractor.isExerciseInProgress()) {
                createExercise()
            }
        }
    }

    private fun observeExerciseState() {
        viewModelScope.launch {
            exerciseInteractor.observeExerciseState().collect { onExerciseEventReceived(it) }
        }
    }

    private suspend fun createExercise() {
        with(exerciseInteractor) {
            clearExercise()
            createExercise()
            startExercise()
        }
    }

    private fun onExerciseEventReceived(state: ExerciseState?) {
        when (state) {
            is ExerciseState.Preparation -> {
                exerciseDuration = state.exerciseDurationSeconds
                secondsRemain.value = state.remainSeconds
                isProgressReversed = false
                updateExerciseProgress()
            }
            is ExerciseState.Holding -> {
                exerciseDuration = state.exerciseDurationSeconds
                repeatsRemain.value = state.repeatRemains
                secondsRemain.value = state.remainSeconds
                isProgressReversed = true
                updateExerciseProgress()
            }
            is ExerciseState.Relax -> {
                exerciseDuration = state.exerciseDurationSeconds
                repeatsRemain.value = state.repeatsRemain
                secondsRemain.value = state.remainSeconds
                isProgressReversed = false
                updateExerciseProgress()
            }
            is ExerciseState.Pause -> {
                repeatsRemain.value = state.repeatsRemain
                secondsRemain.value = state.remainSeconds
                _exercisePlaybackState.value = Paused
            }
            is ExerciseState.Finish -> {
                _exercisePlaybackState.value = Stopped
                handleExerciseFinish(state)
            }
        }
        exerciseKind.value = state?.let { exerciseNameProvider.exerciseName(state) }.orEmpty()
        currentState = state

        if (state?.isPlayingState() == true) {
            _exercisePlaybackState.value = Playing
        }
    }

    private fun updateExerciseProgress() {
        val secondsRemain = secondsRemain.value ?: 0
        exerciseProgress.value = if (isProgressReversed) {
            1 - secondsRemain / (exerciseDuration - 1).toFloat()
        } else {
            secondsRemain / (exerciseDuration - 1).toFloat()
        }
    }

    private fun handleExerciseFinish(state: ExerciseState.Finish) {
        if (!state.isForceFinished) {
            navigateToExerciseResult.value = Event(true)
        } else {
            exit()
        }
        if (isExerciseStoppedFromExerciseScreen) {
            clearNotification()
        }
    }

    private fun clearNotification() {
        if (exerciseSettingsRepository.isNotificationEnabled()) {
            exerciseServiceInteractor.clearNotification()
        }
    }

    private fun ExerciseState.isPlayingState(): Boolean {
        val playingStates = listOf(
            ExerciseState.Preparation::class,
            ExerciseState.Relax::class,
            ExerciseState.Holding::class
        )
        return playingStates.any { it == this::class }
    }

    private fun exit() {
        if (exit.value?.peekContent() != true){
            exit.value = Event(true)
        }
    }

    private fun formatTimeRemains(seconds: Long): String {
        val sec = seconds % 60
        val min = seconds / 60
        return String.format("%02d:%02d", min, sec)
    }
}
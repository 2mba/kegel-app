package org.tumba.kegel_app.ui.exercise

import androidx.annotation.ColorRes
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.ExerciseTracker
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseServiceInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.*
import org.tumba.kegel_app.utils.Empty
import org.tumba.kegel_app.utils.Event
import org.tumba.kegel_app.utils.formatExerciseDuration
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseServiceInteractor: ExerciseServiceInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseParametersProvider: ExerciseParametersProvider,
    private val exerciseNameProvider: ExerciseNameProvider,
    private val resourceProvider: ResourceProvider,
    private val tracker: ExerciseTracker
) : BaseViewModel() {

    val exerciseKind = MutableLiveData(String.Empty)
    val exercisePlaybackState by lazy { _exercisePlaybackState.distinctUntilChanged() }
    val repeats = MutableLiveData(String.Empty)
    val timeRemain by lazy { secondsRemain.map { formatExerciseDuration(it) } }
    val fullTimeRemain by lazy { fullSecondsRemain.map { formatExerciseDuration(it) } }
    val exerciseProgress = MutableLiveData(0F)
    val exerciseProgressColor = MutableLiveData(R.color.transparent)
    val level = exerciseParametersProvider.observeLevel().asLiveData()
    val day = exerciseParametersProvider.observeDay().asLiveData()
    val isVibrationEnabled = exerciseSettingsRepository.isVibrationEnabled
        .asFlow()
        .asLiveData(Dispatchers.Default)
    val isNotificationEnabled = exerciseSettingsRepository.isNotificationEnabled
        .asFlow()
        .asLiveData(Dispatchers.Default)
    val exitConfirmationDialogVisible = MutableLiveData(Event(false))
    val exit = MutableLiveData(Event(false))
    val navigateToExerciseResult = MutableLiveData(Event(false))
    private val _exercisePlaybackState = MutableLiveData(Playing)
    private val secondsRemain = MutableLiveData(0L)
    private val fullSecondsRemain = MutableLiveData(0L)
    private var exerciseDuration = 0L
    private var currentState: ExerciseState? = null
    private var isProgressReversed = true
    private var isExerciseStoppedFromExerciseScreen = false

    init {
        startExerciseIfNoExerciseInProgress()
        observeExerciseState()
    }

    fun onClickPlay() {
        switchPlayback()
    }

    fun onClickStop() {
        tracker.trackStop()
        viewModelScope.launch {
            isExerciseStoppedFromExerciseScreen = true
            exerciseInteractor.stopExercise()
        }
    }

    fun onNotificationStateChanged(enabled: Boolean) {
        if (isNotificationEnabled.value != enabled) {
            tracker.trackChangeNotification(enabled)
        }
        viewModelScope.launch {
            exerciseInteractor.setNotificationEnabled(enabled)
        }
    }

    fun onVibrationStateChanged(enabled: Boolean) {
        if (isVibrationEnabled.value != enabled) {
            tracker.trackChangeVibration(enabled)
        }
        viewModelScope.launch {
            exerciseSettingsRepository.isVibrationEnabled.value = enabled
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
        tracker.exitConfirmed()
        viewModelScope.launch {
            isExerciseStoppedFromExerciseScreen = true
            exerciseInteractor.stopExercise()
        }
    }

    fun onHelpClicked() {
        if (exercisePlaybackState.value == Playing) {
            switchPlayback()
        }
    }

    private fun switchPlayback() {
        when (exercisePlaybackState.value) {
            Playing -> {
                tracker.trackPause()
                viewModelScope.launch {
                    exerciseInteractor.pauseExercise()
                }
            }
            Paused -> {
                tracker.trackPlay()
                viewModelScope.launch {
                    exerciseInteractor.resumeExercise()
                }
            }
            else -> {
            }
        }
    }

    private fun startExerciseIfNoExerciseInProgress() {
        viewModelScope.launch {
            if (!exerciseInteractor.isExerciseInProgress()) {
                createExercise()
                trackExerciseStarted()
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
            is ExerciseState.SingleExercise -> {
                exerciseDuration = state.singleExerciseInfo.exerciseDurationSeconds
                secondsRemain.value = state.singleExerciseInfo.remainSeconds
                fullSecondsRemain.value = state.exerciseInfo.remainSeconds
                repeats.value = formatRepeats(state.exerciseInfo.repeatRemains, state.exerciseInfo.repeats)
                isProgressReversed = isProgressReversed(state)
                updateExerciseProgress()
            }
            is ExerciseState.Pause -> {
                exerciseDuration = state.singleExerciseInfo.exerciseDurationSeconds
                secondsRemain.value = state.singleExerciseInfo.remainSeconds
                repeats.value = formatRepeats(state.exerciseInfo.repeatRemains, state.exerciseInfo.repeats)
                fullSecondsRemain.value = state.exerciseInfo.remainSeconds
                _exercisePlaybackState.value = Paused
            }
            is ExerciseState.Finish -> {
                _exercisePlaybackState.value = Stopped
                handleExerciseFinish(state)
            }
            else -> {
            }
        }
        exerciseKind.value = state?.let { exerciseNameProvider.exerciseName(state) }.orEmpty()
        exerciseProgressColor.value = state?.let { getExerciseProgressColor(state) } ?: R.color.transparent
        currentState = state

        if (state?.isPlayingState() == true) {
            _exercisePlaybackState.value = Playing
        }
    }

    private fun isProgressReversed(state: ExerciseState): Boolean {
        return when (state) {
            is ExerciseState.Holding -> true
            else -> false
        }
    }

    private fun updateExerciseProgress() {
        val secondsRemain = secondsRemain.value ?: 0
        exerciseProgress.value = if (isProgressReversed) {
            1 - secondsRemain / (exerciseDuration).toFloat()
        } else {
            secondsRemain / (exerciseDuration).toFloat()
        }
    }

    private fun handleExerciseFinish(state: ExerciseState.Finish) {
        tracker.trackFinished()
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
        if (exerciseSettingsRepository.isNotificationEnabled.value) {
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
        if (exit.value?.peekContent() != true) {
            exit.value = Event(true)
        }
    }

    private fun formatRepeats(repeatsRemain: Int, repeats: Int): String {
        return "${repeats - repeatsRemain + 1}/$repeats"
    }

    private fun trackExerciseStarted() {
        viewModelScope.launch {
            val (level, day) = combine(
                exerciseParametersProvider.observeLevel(),
                exerciseParametersProvider.observeDay()
            ) { level, day -> level to day }
                .first()
            tracker.trackStarted(level, day)
        }
    }

    @ColorRes
    private fun getExerciseProgressColor(state: ExerciseState): Int {
        return when (state) {
            is ExerciseState.Preparation -> R.color.exerciseColorPreparation
            is ExerciseState.Holding -> R.color.exerciseColorHolding
            is ExerciseState.Relax -> R.color.exerciseColorRelax
            is ExerciseState.Pause -> R.color.exerciseColorPaused
            else -> R.color.transparent
        }
    }
}
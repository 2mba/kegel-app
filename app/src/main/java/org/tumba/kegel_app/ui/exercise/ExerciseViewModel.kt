package org.tumba.kegel_app.ui.exercise

import androidx.annotation.ColorRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.ExerciseTracker
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseServiceInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import org.tumba.kegel_app.ui.exercise.ExercisePlaybackStateUiModel.*
import org.tumba.kegel_app.utils.Event
import org.tumba.kegel_app.utils.formatExerciseDuration
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseServiceInteractor: ExerciseServiceInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val exerciseParametersProvider: ExerciseParametersProvider,
    private val exerciseNameProvider: ExerciseNameProvider,
    private val tracker: ExerciseTracker
) : BaseViewModel() {

    init {
        startExerciseIfNoExerciseInProgress()
    }

    private val exerciseState = exerciseInteractor.observeExerciseState()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val exerciseKind = exerciseState.map { exerciseNameProvider.exerciseName(it) }.asLiveData()
    val exercisePlaybackState by lazy { _exercisePlaybackState.asLiveData() }

    val repeats = exerciseState.map { state ->
        if (state is ExerciseState.SingleExercise) {
            formatRepeats(state.exerciseInfo.repeatRemains, state.exerciseInfo.repeats)
        } else {
            "0"
        }
    }.asLiveData()

    private val secondsRemain = exerciseState.map { state ->
        if (state is ExerciseState.SingleExercise) state.singleExerciseInfo.remainSeconds else 0
    }
    private val fullSecondsRemain = exerciseState.map { state ->
        if (state is ExerciseState.SingleExercise) state.exerciseInfo.remainSeconds else 0
    }
    val timeRemain = secondsRemain.map { formatExerciseDuration(it) }.asLiveData()
    val fullTimeRemain = fullSecondsRemain.map { formatExerciseDuration(it) }.asLiveData()
    val exerciseProgress = exerciseState.map { state ->
        if (state is ExerciseState.SingleExercise) calculateProgress(state) else 0F
    }.asLiveData()

    private val _exercisePlaybackState = exerciseState.map { state ->
        when (state) {
            is ExerciseState.Pause -> Paused
            is ExerciseState.SingleExercise -> Playing
            is ExerciseState.Finish -> Stopped
            else -> Playing
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Playing)

    val exerciseProgressColor = exerciseState.map { getExerciseProgressColor(it) }.asLiveData()
    val level = exerciseParametersProvider.observeLevel().asLiveData()
    val day = exerciseParametersProvider.observeDay().asLiveData()
    val isVibrationEnabled = exerciseSettingsRepository.isVibrationEnabled
        .asFlow()
        .asLiveData(Dispatchers.Default)
    val isNotificationEnabled = exerciseSettingsRepository.isNotificationEnabled
        .asFlow()
        .asLiveData(Dispatchers.Default)
    val isSoundEnabled = exerciseSettingsRepository.isSoundEnabled
        .asFlow()
        .asLiveData(Dispatchers.Default)
    val exitConfirmationDialogVisible = MutableLiveData(Event(false))
    val exit = MutableLiveData(Event(false))
    val navigateToExerciseResult = MutableLiveData(Event(false))
    private var exerciseDuration = exerciseState.filterIsInstance<ExerciseState.SingleExercise>()
        .map { it.singleExerciseInfo.exerciseDurationSeconds }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    private var isExerciseStoppedFromExerciseScreen = false

    init {
        ExerciseFinishHandler().observeExerciseState(exerciseState)
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

    fun onSoundStateChanged(enabled: Boolean) {
        if (isSoundEnabled.value != enabled) {
            tracker.trackChangeSound(enabled)
        }
        viewModelScope.launch {
            exerciseSettingsRepository.isSoundEnabled.value = enabled
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

    private suspend fun createExercise() {
        with(exerciseInteractor) {
            clearExercise()
            createExercise()
            startExercise()
        }
    }

    private fun isProgressReversed(state: ExerciseState): Boolean {
        return when (state) {
            is ExerciseState.Holding -> true
            else -> false
        }
    }

    private fun clearNotification() {
        if (exerciseSettingsRepository.isNotificationEnabled.value) {
            exerciseServiceInteractor.clearNotification()
        }
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


    private fun calculateProgress(state: ExerciseState): Float {
        val secondsRemain = (state as? ExerciseState.SingleExercise)?.singleExerciseInfo?.remainSeconds ?: 0
        return if (isProgressReversed(state)) {
            1 - secondsRemain / (exerciseDuration.value).toFloat()
        } else {
            secondsRemain / (exerciseDuration.value).toFloat()
        }
    }

    private inner class ExerciseFinishHandler {

        fun observeExerciseState(exerciseState: SharedFlow<ExerciseState>) {
            viewModelScope.launch {
                exerciseState
                    .filterIsInstance<ExerciseState.Finish>()
                    .collect(::handleExerciseFinish)
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
    }
}
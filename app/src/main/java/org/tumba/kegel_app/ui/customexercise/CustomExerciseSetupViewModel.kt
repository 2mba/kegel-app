package org.tumba.kegel_app.ui.customexercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.tumba.kegel_app.analytics.CustomExerciseSetupTracker
import org.tumba.kegel_app.domain.interactor.CustomExerciseInteractor
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.exercise.ExerciseType
import org.tumba.kegel_app.utils.Event
import javax.inject.Inject

class CustomExerciseSetupViewModel @Inject constructor(
    private val customExerciseInteractor: CustomExerciseInteractor,
    private val exerciseInteractor: ExerciseInteractor,
    private val tracker: CustomExerciseSetupTracker
) : BaseViewModel() {

    private val _tenseDuration = MutableStateFlow(0)
    val tenseDuration = _tenseDuration.asLiveData()

    private val _relaxDuration = MutableStateFlow(0)
    val relaxDuration = _relaxDuration.asLiveData()

    private val _repeats = MutableStateFlow(0)
    val repeats = _repeats.asLiveData()

    val duration = combine(_tenseDuration, _relaxDuration, _repeats, ::calculateDuration)
        .map { duration -> String.format("%02d:%02d", duration / SECONDS_IN_MINUTE, duration % SECONDS_IN_MINUTE) }
        .asLiveData()

    val showTenseDurationSelectorDialog = MutableLiveData<Event<Boolean>>()
    val showRelaxDurationSelectorDialog = MutableLiveData<Event<Boolean>>()
    val showRepeatsSelectorDialog = MutableLiveData<Event<Boolean>>()

    init {
        tracker.trackCustomExerciseSetupOpened()
        viewModelScope.launch {
            _tenseDuration.value = customExerciseInteractor.observeTenseDuration().first()
            _relaxDuration.value = customExerciseInteractor.observeRelaxDuration().first()
            _repeats.value = customExerciseInteractor.observeRepeats().first()
        }
    }

    fun onTenseDurationClicked() {
        tracker.trackSetTenseDurationClicked()
        showTenseDurationSelectorDialog.value = Event(true)
    }

    fun onRelaxDurationSelectorDialog() {
        tracker.trackSetRelaxDurationClicked()
        showRelaxDurationSelectorDialog.value = Event(true)
    }

    fun onRepeatsClicked() {
        tracker.trackSetRepeatsClicked()
        showRepeatsSelectorDialog.value = Event(true)
    }

    fun onTenseDurationSelected(duration: Int) {
        _tenseDuration.value = duration
    }

    fun onRelaxDurationSelected(duration: Int) {
        _relaxDuration.value = duration
    }

    fun onRepeatsSelected(repeats: Int) {
        _repeats.value = repeats
    }

    fun onStartClicked() {
        tracker.trackStartClicked()
        if (exerciseInteractor.isThereCompletedExercise() || exerciseInteractor.isFirstExerciseChallengeShown()) {
            viewModelScope.launch {
                saveCustomExercise()
                navigate(
                    CustomExerciseSetupFragmentDirections.actionCustomExerciseSetupFragmentToScreenExercise(
                        ExerciseType.Custom
                    )
                )
            }
        } else {
            viewModelScope.launch {
                exerciseInteractor.setFirstExerciseChallengeShown()
                saveCustomExercise()
                navigate(
                    CustomExerciseSetupFragmentDirections.actionCustomExerciseSetupFragmentToFirstExerciseChallengeDialogFragment(
                        ExerciseType.Custom
                    )
                )
            }
        }
    }

    private suspend fun saveCustomExercise() {
        customExerciseInteractor.setTenseDuration(_tenseDuration.value)
        customExerciseInteractor.setRelaxDuration(_relaxDuration.value)
        customExerciseInteractor.setRepeats(_repeats.value)
    }

    private fun calculateDuration(tense: Int, relax: Int, repeats: Int): Int {
        return (tense + relax) * repeats
    }

    companion object {
        private const val SECONDS_IN_MINUTE = 60
    }
}

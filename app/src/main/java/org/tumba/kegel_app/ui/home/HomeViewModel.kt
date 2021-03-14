package org.tumba.kegel_app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.utils.Event
import org.tumba.kegel_app.utils.formatExerciseDuration
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class HomeViewModel @Inject constructor(
    exerciseParametersProvider: ExerciseParametersProvider,
    private val progressViewedStore: ProgressViewedStore
) : BaseViewModel() {

    val exerciseLevel = exerciseParametersProvider.observeLevel().asLiveData()
    val exerciseDay = exerciseParametersProvider.observeDay().asLiveData()
    val numberOfExercises = exerciseParametersProvider.observeNumberOfCompletedExercises().asLiveData()
    val allExercisesDuration = exerciseParametersProvider.observeAllExercisesDurationInSeconds()
        .map { it.seconds.toInt(DurationUnit.MINUTES) }
        .asLiveData()

    val nextExercisesDuration = exerciseParametersProvider.observeNextExerciseDurationInSeconds()
        .map(::formatExerciseDuration)
        .asLiveData()

    val progress = exerciseParametersProvider.observeProgress().asLiveData()
    val progressAnimation = MutableLiveData<Event<Boolean>>()

    fun onResume() {
        if (!progressViewedStore.isProgressViewed) {
            progressViewedStore.isProgressViewed = true
            progressAnimation.value = Event(true)
        }
    }

    fun onStartExerciseClicked() {
        navigate(HomeFragmentDirections.actionScreenHomeToScreenExercise())
    }

    fun onShowHintClicked() {
        navigate(HomeFragmentDirections.actionScreenHomeToExerciseInfoFragment())
    }
}


package org.tumba.kegel_app.ui.home

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class HomeViewModel @Inject constructor(
    exerciseParametersProvider: ExerciseParametersProvider
) : BaseViewModel() {

    val exerciseLevel = exerciseParametersProvider.observeLevel().asLiveData()
    val exerciseDay = exerciseParametersProvider.observeDay().asLiveData()
    val numberOfExercises = exerciseParametersProvider.observeNumberOfCompletedExercises().asLiveData()
    val exercisesDuration = exerciseParametersProvider.observeExercisesDurationInSeconds()
        .map { it.seconds.toInt(DurationUnit.MINUTES) }
        .asLiveData()

    fun onStartExerciseClicked() {
        navigate(HomeFragmentDirections.actionScreenHomeToScreenExercise())
    }

    fun onShowHintClicked() {
        navigate(HomeFragmentDirections.actionScreenHomeToExerciseInfoFragment())
    }
}


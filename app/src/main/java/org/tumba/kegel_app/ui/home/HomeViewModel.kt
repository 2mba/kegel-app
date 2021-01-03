package org.tumba.kegel_app.ui.home

import androidx.lifecycle.asLiveData
import org.tumba.kegel_app.domain.ExerciseParametersProvider
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    exerciseParametersProvider: ExerciseParametersProvider
) : BaseViewModel() {

    val exerciseLevel = exerciseParametersProvider.observeLevel().asLiveData()
    val exerciseDay = exerciseParametersProvider.observeDay().asLiveData()
    val numberOfExercises = exerciseParametersProvider.observeNumberOfCompletedExercises().asLiveData()
    val exercisesDuration = exerciseParametersProvider.observeExercisesDurationInSeconds().asLiveData()
}


package org.tumba.kegel_app.ui.home

import androidx.lifecycle.map
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    exerciseSettingsRepository: ExerciseSettingsRepository
) : BaseViewModel() {

    val exerciseLevel = exerciseSettingsRepository.observeExerciseLevel()
    val exerciseDay = exerciseSettingsRepository.getExerciseDay()
    val numberOfExercises = exerciseSettingsRepository.observeNumberOfCompletedExercises()
    val exercisesDuration = exerciseSettingsRepository.observeExercisesDurationSeconds().map { it / 60 }
}


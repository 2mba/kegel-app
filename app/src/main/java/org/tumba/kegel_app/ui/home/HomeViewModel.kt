package org.tumba.kegel_app.ui.home

import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel

class HomeViewModel(
    exerciseSettingsRepository: ExerciseSettingsRepository
) : BaseViewModel() {

    val exerciseLevel = exerciseSettingsRepository.getExerciseLevel()
    val exerciseDay = exerciseSettingsRepository.getExerciseDay()
}


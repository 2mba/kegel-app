package org.tumba.kegel_app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.exercise.ExerciseSettingsInteractor

class HomeViewModelFactory(
    private val exerciseSettingsInteractor: ExerciseSettingsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(exerciseSettingsInteractor) as T
    }
}
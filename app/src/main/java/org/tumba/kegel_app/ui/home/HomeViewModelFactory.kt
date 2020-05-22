package org.tumba.kegel_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.repository.ExerciseSettingsRepository

class HomeViewModelFactory(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(exerciseSettingsRepository) as T
    }
}
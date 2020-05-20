package org.tumba.kegel_app.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.core.system.IResourceProvider

class ExerciseViewModelFactory(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseSettingsInteractor: ExerciseSettingsInteractor,
    private val resourceProvider: IResourceProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExerciseViewModel(exerciseInteractor, exerciseSettingsInteractor, resourceProvider) as T
    }
}
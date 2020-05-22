package org.tumba.kegel_app.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.core.system.IResourceProvider
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository

class ExerciseViewModelFactory(
    private val exerciseInteractor: ExerciseInteractor,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val resourceProvider: IResourceProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExerciseViewModel(
            exerciseInteractor,
            exerciseSettingsRepository,
            resourceProvider
        ) as T
    }
}
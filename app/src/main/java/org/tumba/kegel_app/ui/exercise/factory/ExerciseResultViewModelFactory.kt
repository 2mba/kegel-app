package org.tumba.kegel_app.ui.exercise.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.tumba.kegel_app.di.Di

class ExerciseResultViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return Di.appComponent.getExerciseResultViewModel() as T
    }
}
package org.tumba.kegel_app.home.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.Observables
import org.tumba.kegel_app.exercise.core.presentation.CoreViewModel
import org.tumba.kegel_app.exercise.core.presentation.disposeOnDestroy
import org.tumba.kegel_app.exercise.utils.async
import org.tumba.kegel_app.home.domain.ExerciseSettingsInteractor
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val exerciseSettingsInteractor: ExerciseSettingsInteractor
) : CoreViewModel() {

    val exerciseLevel = MutableLiveData(0)
    val exerciseDay = MutableLiveData(0)

    init {
        fetchExerciseData()
    }

    private fun fetchExerciseData() {
        Observables.zip(
            exerciseSettingsInteractor.getExerciseLevel(),
            exerciseSettingsInteractor.getExerciseDay()
        )
            .firstElement()
            .async()
            .subscribe { (exerciseLevel, exerciseDay) ->
                this.exerciseLevel.value = exerciseLevel
                this.exerciseDay.value = exerciseDay
            }
            .disposeOnDestroy(this)
    }
}
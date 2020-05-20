package org.tumba.kegel_app.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.Observables
import org.tumba.kegel_app.exercise.ExerciseSettingsInteractor
import org.tumba.kegel_app.utils.async

class HomeViewModel(
    private val exerciseSettingsInteractor: ExerciseSettingsInteractor
) : ViewModel() {

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
            //.disposeOnDestroy(this)
    }
}


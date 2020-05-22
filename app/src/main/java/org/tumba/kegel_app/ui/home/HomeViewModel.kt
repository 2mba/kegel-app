package org.tumba.kegel_app.ui.home

import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.Observables
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.disposeOnDestroy
import org.tumba.kegel_app.utils.async

class HomeViewModel(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) : BaseViewModel() {

    val exerciseLevel = MutableLiveData(0)
    val exerciseDay = MutableLiveData(0)

    init {
        fetchExerciseData()
    }

    private fun fetchExerciseData() {
        Observables.zip(
            exerciseSettingsRepository.getExerciseLevel(),
            exerciseSettingsRepository.getExerciseDay()
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


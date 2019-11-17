package org.tumba.kegel_app.exercise.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.subscribeBy
import org.tumba.kegel_app.exercise.domain.entity.ExerciseConfig
import org.tumba.kegel_app.exercise.domain.entity.Time
import org.tumba.kegel_app.exercise.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.exercise.utils.EMPTY
import org.tumba.kegel_app.exercise.utils.async
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor
) : ViewModel() {

    val logs = MutableLiveData<String>(String.EMPTY)

    init {
        exerciseInteractor.createExercise(
            config = ExerciseConfig(
                preparationDuration = Time(5, TimeUnit.SECONDS),
                holdingDuration = Time(5, TimeUnit.SECONDS),
                relaxDuration = Time(2, TimeUnit.SECONDS),
                repeats = 2
            )
        )
            .andThen(exerciseInteractor.startExercise())
            .andThen(exerciseInteractor.subscribeToExerciseEvents())
            .async()
            .subscribeBy(
                onNext = { logs.value = it.toString() }
            )
    }
}
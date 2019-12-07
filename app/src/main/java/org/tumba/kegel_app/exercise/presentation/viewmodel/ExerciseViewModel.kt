package org.tumba.kegel_app.exercise.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.tumba.kegel_app.exercise.domain.entity.ExerciseConfig
import org.tumba.kegel_app.exercise.domain.entity.ExerciseEvent
import org.tumba.kegel_app.exercise.domain.entity.Time
import org.tumba.kegel_app.exercise.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.exercise.utils.EMPTY
import org.tumba.kegel_app.exercise.utils.async
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor
) : ViewModel() {

    val exercise = MutableLiveData<String>(String.EMPTY)
    val repeatsRemain = MutableLiveData(0)
    val secondsRemain = MutableLiveData(0L)
    val fullRemainPart = MutableLiveData(1F)

    init {
        exerciseInteractor.createExercise(
            config = ExerciseConfig(
                preparationDuration = Time(3, TimeUnit.SECONDS),
                holdingDuration = Time(8, TimeUnit.SECONDS),
                relaxDuration = Time(5, TimeUnit.SECONDS),
                repeats = 5
            )
        )
            .andThen(exerciseInteractor.startExercise())
            .andThen(exerciseInteractor.subscribeToExerciseEvents())
            .async()
            .subscribeBy(
                onNext = {
                    when (it) {
                        is ExerciseEvent.Preparation -> {
                            exercise.value = "Подготовка"
                        }
                        is ExerciseEvent.Holding -> {
                            exercise.value = "Сжатие"
                            repeatsRemain.value = it.repeatRemains
                            secondsRemain.value = it.remainSeconds
                        }
                        is ExerciseEvent.Relax -> {
                            exercise.value = "Отдых"
                            repeatsRemain.value = it.repeatsRemain
                            secondsRemain.value = it.remainSeconds
                        }
                    }
                }
            )

        Observable.interval(50, TimeUnit.MILLISECONDS)
            .async()
            .subscribe { fullRemainPart.value = fullRemainPart.value?.minus(0.02F) }
    }
}
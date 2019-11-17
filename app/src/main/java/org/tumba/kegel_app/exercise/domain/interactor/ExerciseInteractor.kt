package org.tumba.kegel_app.exercise.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import org.tumba.kegel_app.exercise.data.ExerciseRepository
import org.tumba.kegel_app.exercise.domain.entity.Exercise
import org.tumba.kegel_app.exercise.domain.entity.ExerciseConfig
import org.tumba.kegel_app.exercise.domain.entity.ExerciseEvent
import javax.inject.Inject

class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {

    fun createExercise(config: ExerciseConfig): Completable {
        return Completable.defer {
            val exercise = Exercise(config)
            exerciseRepository.saveExercise(exercise)
        }
    }

    fun subscribeToExerciseEvents(): Observable<ExerciseEvent> {
        return exerciseRepository.getExercise()
            .flatMapObservable { it.events }
    }

    fun startExercise(): Completable {
        return exerciseRepository.getExercise()
            .map { it.start() }
            .ignoreElement()
    }
}
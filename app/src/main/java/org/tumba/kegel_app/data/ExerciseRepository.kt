package org.tumba.kegel_app.data

import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ExerciseRepository @Inject constructor() {

    private var exercise: Exercise? = null

    fun getExercise(): Maybe<Exercise> {
        return Maybe.defer {
            if (exercise != null) {
                Maybe.just(exercise)
            } else {
                Maybe.empty()
            }
        }
    }

    fun saveExercise(exercise: Exercise): Completable {
        return Completable.fromAction {
            this.exercise = exercise
        }
    }
}
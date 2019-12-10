package org.tumba.kegel_app.exercise.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import org.tumba.kegel_app.exercise.domain.entity.Exercise
import javax.inject.Inject

class ExerciseRepository @Inject constructor() {

    private var exercise: Exercise? = null
    private var isVibrationEnabled: Boolean = true

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

    fun setVibrationEnabled(enabled: Boolean): Completable {
        return Completable.fromAction {
            isVibrationEnabled = enabled
        }
    }

    fun isVibrationEnabled(): Observable<Boolean> {
        return Observable.just(isVibrationEnabled)
    }
}
package org.tumba.kegel_app.exercise.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import org.tumba.kegel_app.core.system.IVibrationManager
import org.tumba.kegel_app.exercise.data.ExerciseRepository
import org.tumba.kegel_app.exercise.data.ExerciseSettingsRepository
import org.tumba.kegel_app.exercise.domain.entity.Exercise
import org.tumba.kegel_app.exercise.domain.entity.ExerciseConfig
import org.tumba.kegel_app.exercise.domain.entity.ExerciseEvent
import javax.inject.Inject

class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val vibrationManager: IVibrationManager
) {

    fun createExercise(config: ExerciseConfig): Completable {
        return Completable.defer {
            exerciseRepository.saveExercise(createExerciseFrom(config))
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

    fun stopExercise(): Completable {
        return exerciseRepository.getExercise()
            .map { it.stop() }
            .ignoreElement()
    }

    fun pauseExercise(): Completable {
        return exerciseRepository.getExercise()
            .map { it.pause() }
            .ignoreElement()
    }

    fun resumeExercise(): Completable {
        return exerciseRepository.getExercise()
            .map { it.resume() }
            .ignoreElement()
    }

    private fun createExerciseFrom(config: ExerciseConfig): Exercise {
        return Exercise(
            config = config,
            vibrationManager = vibrationManager,
            vibrationEnabledStateProvider = {
                exerciseSettingsRepository.isVibrationEnabled().blockingFirst()
            }
        )
    }
}
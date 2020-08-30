package org.tumba.kegel_app.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.tumba.kegel_app.domain.Exercise

class ExerciseRepository {

    private var exercise: Exercise? = null

    suspend fun getExercise(): Exercise? {
        return exercise
    }

    suspend fun saveExercise(exercise: Exercise) {
        this@ExerciseRepository.exercise = exercise
    }
}
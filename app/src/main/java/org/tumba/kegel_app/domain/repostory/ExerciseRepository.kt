package org.tumba.kegel_app.domain.repostory

import org.tumba.kegel_app.db.CompletedExerciseDao
import org.tumba.kegel_app.db.CompletedExerciseEntity
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val completedExerciseDao: CompletedExerciseDao
){

    fun getAll(): List<CompletedExerciseEntity> = completedExerciseDao.getAll()

    fun insertAll(vararg exercises: CompletedExerciseEntity) {
        completedExerciseDao.insertAll()
    }

    fun delete(exercise: CompletedExerciseEntity) {
        completedExerciseDao.delete(exercise)
    }
}
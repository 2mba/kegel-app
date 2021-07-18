package org.tumba.kegel_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CompletedExerciseDao {
    @Query("SELECT * FROM completed_exercise")
    fun getAll(): List<CompletedExerciseEntity>

    @Insert
    fun insertAll(vararg exercises: CompletedExerciseEntity)

    @Delete
    fun delete(exercise: CompletedExerciseEntity)
}
package org.tumba.kegel_app.repository

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.tumba.kegel_app.domain.Exercise

@Suppress("EXPERIMENTAL_API_USAGE")
class ExerciseRepository {

    private var exerciseChannel = BroadcastChannel<Exercise?>(Channel.CONFLATED)

    init {
        runBlocking { exerciseChannel.send(null) }
    }

    fun observeExercise(): Flow<Exercise?> {
        return exerciseChannel.asFlow()
    }

    suspend fun saveExercise(exercise: Exercise) {
        exerciseChannel.send(exercise)
    }

    suspend fun deleteExercise() {
        exerciseChannel.send(null)
    }
}
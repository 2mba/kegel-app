package org.tumba.kegel_app.domain

sealed class ExerciseState {

    object NotStarted : ExerciseState()

    data class Preparation(
        val remainSeconds: Long,
        val exerciseDurationSeconds: Long
    ) : ExerciseState()

    data class Holding(
        val remainSeconds: Long,
        val repeatRemains: Int,
        val exerciseDurationSeconds: Long
    ) : ExerciseState()

    data class Relax(
        val remainSeconds: Long,
        val repeatsRemain: Int,
        val exerciseDurationSeconds: Long
    ) : ExerciseState()

    data class Pause(val remainSeconds: Long, val repeatsRemain: Int) : ExerciseState()

    data class Finish(val isForceFinished: Boolean): ExerciseState()
}

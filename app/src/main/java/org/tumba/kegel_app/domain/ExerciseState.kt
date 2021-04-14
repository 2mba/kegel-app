package org.tumba.kegel_app.domain

sealed class ExerciseState {

    object NotStarted : ExerciseState()

    interface SingleExercise {
        val singleExerciseInfo: SingleExerciseInfo
        val exerciseInfo: ExerciseInfo
    }

    class Preparation(
        override val singleExerciseInfo: SingleExerciseInfo,
        override val exerciseInfo: ExerciseInfo
    ) : ExerciseState(), SingleExercise

    data class Holding(
        override val singleExerciseInfo: SingleExerciseInfo,
        override val exerciseInfo: ExerciseInfo
    ) : ExerciseState(), SingleExercise

    data class Relax(
        override val singleExerciseInfo: SingleExerciseInfo,
        override val exerciseInfo: ExerciseInfo
    ) : ExerciseState(), SingleExercise

    data class Pause(
        override val singleExerciseInfo: SingleExerciseInfo,
        override val exerciseInfo: ExerciseInfo
    ) : ExerciseState(), SingleExercise

    data class Finish(
        val isForceFinished: Boolean,
        val exerciseInfo: ExerciseInfo
    ) : ExerciseState()
}

data class SingleExerciseInfo(
    val remainSeconds: Long,
    val exerciseDurationSeconds: Long
)

data class ExerciseInfo(
    val remainSeconds: Long,
    val repeatRemains: Int,
    val durationSeconds: Long,
    val repeats: Int
)
package org.tumba.kegel_app.factory

import org.tumba.kegel_app.domain.ExerciseInfo
import org.tumba.kegel_app.domain.SingleExerciseInfo
import org.tumba.kegel_app.util.TestRandom

object ExerciseStateFactory {

    fun singleExerciseInfo(
        remainSeconds: Long = TestRandom.instance.nextLong(1000),
        exerciseDurationSeconds: Long = TestRandom.instance.nextLong(1000)
    ): SingleExerciseInfo {
        return SingleExerciseInfo(
            remainSeconds = remainSeconds,
            exerciseDurationSeconds = exerciseDurationSeconds
        )
    }

    fun exerciseInfo(
        remainSeconds: Long = TestRandom.instance.nextLong(1000),
        repeatRemains: Int = TestRandom.instance.nextInt(1000),
        durationSeconds: Long = TestRandom.instance.nextLong(1000),
        repeats: Int = TestRandom.instance.nextInt(1000)
    ): ExerciseInfo {
        return ExerciseInfo(
            remainSeconds = remainSeconds,
            repeatRemains = repeatRemains,
            durationSeconds = durationSeconds,
            repeats = repeats
        )
    }
}
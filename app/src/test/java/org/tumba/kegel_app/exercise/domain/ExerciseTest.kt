package org.tumba.kegel_app.exercise.domain

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.Test
import org.tumba.kegel_app.data.Exercise
import org.tumba.kegel_app.data.ExerciseConfig
import org.tumba.kegel_app.data.ExerciseEvent
import org.tumba.kegel_app.data.Time
import java.util.concurrent.TimeUnit

class ExerciseTest {

    companion object {
        private const val AWAIT_TIMEOUT_MILLIS = 20 * 1000L
    }

    @Test
    fun `should repeat one time and receive all events`() {
        val exercise = Exercise(
            ExerciseConfig(
                preparationDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                holdingDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                relaxDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                repeats = 1
            )
        )

        val testObserver = exercise.events
            .doOnNext { println(it) }
            .test()

        exercise.start()

        val events = testObserver
            .awaitCount(4)
            .values()

        events.size `should be equal to` 4
        events[0] `should equal` ExerciseEvent.Preparation(remainSeconds = 1)
        events[1] `should equal` ExerciseEvent.Holding(repeatRemains = 0, remainSeconds = 1)
        events[2] `should equal` ExerciseEvent.Relax(repeatsRemain = 0, remainSeconds = 1)
        events[3] `should equal` ExerciseEvent.Finish
    }

    @Test
    fun `should repeat three time and receive 3 holding and 3 relax events`() {
        val exercise = Exercise(
            ExerciseConfig(
                preparationDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                holdingDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                relaxDuration = Time(
                    1,
                    TimeUnit.SECONDS
                ),
                repeats = 3
            )
        )

        val testObserver = exercise.events
            .doOnNext { println(it) }
            .test()

        exercise.start()

        val events = testObserver
            .awaitCount(3 * 2 + 2, { }, AWAIT_TIMEOUT_MILLIS)
            .values()

        events.count { it is ExerciseEvent.Preparation } `should equal` 1
        events.count { it is ExerciseEvent.Holding } `should equal` 3
        events.count { it is ExerciseEvent.Relax } `should equal` 3
        events.last() `should equal` ExerciseEvent.Finish
    }
}
package org.tumba.kegel_app.domain

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import org.tumba.kegel_app.core.system.Preference
import org.tumba.kegel_app.repository.ExerciseSettingsRepository

class ExerciseProgramTest {

    private lateinit var exerciseProgram: ExerciseProgram

    @RelaxedMockK
    private lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    private lateinit var level: TestPreference<Int>
    private lateinit var day: TestPreference<Int>
    private lateinit var numberOfExercises: TestPreference<Int>
    private lateinit var lastExerciseDate: TestPreference<Long>
    private lateinit var duration: TestPreference<Long>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        exerciseProgram = ExerciseProgram(exerciseSettingsRepository)
        level = TestPreference(value = 1)
        day = TestPreference(value = 1)
        numberOfExercises = TestPreference(value = 0)
        lastExerciseDate = TestPreference(value = 0L)
        duration = TestPreference(value = 0L)

        every { exerciseSettingsRepository.exerciseLevel } returns level
        every { exerciseSettingsRepository.exerciseDay } returns day
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns numberOfExercises
        every { exerciseSettingsRepository.lastCompletedPredefinedExerciseDate } returns lastExerciseDate
        every { exerciseSettingsRepository.exercisesDurationInSeconds } returns duration
    }

    @Test
    fun `should return min config`(): Unit = runBlocking {
        level.value = 1
        day.value = 1

        val config = exerciseProgram.getConfig()
        config.holdingDuration.quantity `should be equal to` 3L
        config.relaxDuration.quantity `should be equal to` 3L
        config.repeats `should be equal to` 10
    }

    @Test
    fun `should return max config`(): Unit = runBlocking {
        level.value = 20
        day.value = 7

        val config = exerciseProgram.getConfig()
        config.holdingDuration.quantity `should be equal to` 20L
        config.relaxDuration.quantity `should be equal to` 5L
        config.repeats `should be equal to` 10
    }

    private class TestPreference<T>(override var value: T, override val key: String = "") : Preference<T> {

        override fun asFlow(): Flow<T> {
            throw IllegalStateException("Not implemented")
        }
    }
}
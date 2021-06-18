package org.tumba.kegel_app.domain

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.util.TestPreference
import org.tumba.kegel_app.utils.DateTimeHelper
import java.util.*

class ExerciseFinishHandlerTest {

    private lateinit var exerciseFinishHandler: ExerciseFinishHandler

    @RelaxedMockK
    private lateinit var dateTimeHelper: DateTimeHelper

    @RelaxedMockK
    private lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    @RelaxedMockK
    private lateinit var exerciseParametersProvider: ExerciseParametersProvider

    private lateinit var level: TestPreference<Int>
    private lateinit var day: TestPreference<Int>
    private lateinit var numberOfExercises: TestPreference<Int>
    private lateinit var lastExerciseDate: TestPreference<Long>
    private lateinit var duration: TestPreference<Long>


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        exerciseFinishHandler = ExerciseFinishHandler(exerciseSettingsRepository, exerciseParametersProvider)

        level = TestPreference(value = 1)
        day = TestPreference(value = 1)
        numberOfExercises = TestPreference(value = 0)
        lastExerciseDate = TestPreference(value = 0L)
        duration = TestPreference(value = 0L)

        every { exerciseParametersProvider.observeDay() } returns flowOf(1)
        every { exerciseSettingsRepository.exerciseLevel } returns level
        every { exerciseSettingsRepository.exerciseDay } returns day
        every { exerciseSettingsRepository.numberOfCompletedExercises } returns numberOfExercises
        every { exerciseSettingsRepository.lastCompletedPredefinedExerciseDate } returns lastExerciseDate
        every { exerciseSettingsRepository.exercisesDurationInSeconds } returns duration
        every { dateTimeHelper.now() } returns Calendar.getInstance()
    }

    @Test
    fun `should not increase level and day if all exercises finished on same day`(): Unit = runBlocking {
        // Arrange
        val nowTimes = (0..10).map { idx ->
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, idx + 1)
            }
        }
        every { dateTimeHelper.now() } returnsMany nowTimes
        // Act
        repeat(10) {
            val state = ExerciseState.Finish(
                isForceFinished = false,
                exerciseInfo = ExerciseInfo(remainSeconds = 0, repeats = 0, repeatRemains = 0, durationSeconds = 100)
            )
            exerciseFinishHandler.onExerciseStateChanged(state)
        }
        // Assert
        level.value `should be equal to` 1
        day.value `should be equal to` 1
    }

}
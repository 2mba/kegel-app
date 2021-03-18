package org.tumba.kegel_app.domain

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.util.TestPreference
import org.tumba.kegel_app.utils.DateTimeHelper
import java.util.*

class ExerciseParametersProviderTest {

    @RelaxedMockK
    private lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    @RelaxedMockK
    private lateinit var day: TestPreference<Int>

    @RelaxedMockK
    private lateinit var lastCompletedExerciseDate: TestPreference<Long>

    @RelaxedMockK
    private lateinit var dateTimeHelper: DateTimeHelper

    private lateinit var exerciseParametersProvider: ExerciseParametersProvider

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { dateTimeHelper.now() } returns Calendar.getInstance()
        exerciseParametersProvider = ExerciseParametersProvider(exerciseSettingsRepository, dateTimeHelper, mock())
    }

    @Test
    fun `should return correct progress if no exercises had been done`(): Unit = runBlocking {
        // Arrange
        day = TestPreference(value = 1)
        lastCompletedExerciseDate = TestPreference(value = 0)
        mockExerciseSettingsRepository()
        // Act
        val progress = exerciseParametersProvider.observeProgress().first()
        // Assert
        // Days     1 2 3 4 5 6 7
        // Value    N 2 3 4 5 6 7
        progress.completed `should be equal to` 0
        progress.next `should equal` 0 // 1st day
    }

    @Test
    fun `should return correct progress if exercise have been completed now`(): Unit = runBlocking {
        // Arrange
        day = TestPreference(value = 1)
        lastCompletedExerciseDate = TestPreference(value = System.currentTimeMillis())
        mockExerciseSettingsRepository()
        // Act
        val progress = exerciseParametersProvider.observeProgress().first()
        // Assert
        // Days     1 2 3 4 5 6 7
        // Value    x 2 3 4 5 6 7
        progress.completed `should be equal to` 1
        progress.next `should equal` null
    }

    @Test
    fun `should return correct progress if exercise have been completed yesterday`(): Unit = runBlocking {
        // Arrange
        day = TestPreference(value = 3)
        lastCompletedExerciseDate = TestPreference(
            value = Calendar.getInstance().let { calendar ->
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.timeInMillis
            }
        )
        mockExerciseSettingsRepository()
        // Act
        val progress = exerciseParametersProvider.observeProgress().first()
        // Assert
        // Days     1 2 3 4 5 6 7
        // Value    x x x N 5 6 7
        progress.completed `should be equal to` 3 // 1st 2nd 3th days completed
        progress.next `should equal` 3 // 4th day
    }

    @Test
    fun `should return correct progress if all days in last week are completed`(): Unit = runBlocking {
        // Arrange
        day = TestPreference(value = 7)
        lastCompletedExerciseDate = TestPreference(
            value = Calendar.getInstance().let { calendar ->
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.timeInMillis
            }
        )
        mockExerciseSettingsRepository()
        // Act
        val progress = exerciseParametersProvider.observeProgress().first()
        // Assert
        // Days     1 2 3 4 5 6 7
        // Value    N 2 3 4 5 6 7
        progress.completed `should be equal to` 0
        progress.next `should equal` 0
    }

    @Test
    fun `should return correct progress if all days in current week are completed`(): Unit = runBlocking {
        // Arrange
        day = TestPreference(value = 7)
        lastCompletedExerciseDate = TestPreference(value = Calendar.getInstance().timeInMillis)
        mockExerciseSettingsRepository()
        // Act
        val progress = exerciseParametersProvider.observeProgress().first()
        // Assert
        // Days     1 2 3 4 5 6 7
        // Value    1 2 3 4 5 6 7
        progress.completed `should be equal to` 0
        progress.next `should equal` null
    }

    private fun mockExerciseSettingsRepository() {
        every { exerciseSettingsRepository.exerciseDay } returns day
        every { exerciseSettingsRepository.lastCompletedExerciseDate } returns lastCompletedExerciseDate
    }
}
package org.tumba.kegel_app.worker

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import org.amshove.kluent.`should be equal to`
import org.junit.Before
import org.junit.Test
import org.tumba.kegel_app.core.system.Preference
import org.tumba.kegel_app.domain.interactor.ReminderDaysEncoderDecoder
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.utils.DateHelper
import java.util.*

class ReminderScheduleProviderTest {

    @RelaxedMockK
    lateinit var dateHelper: DateHelper

    @RelaxedMockK
    lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

    @RelaxedMockK
    lateinit var isReminderEnabled: Preference<Boolean>

    @RelaxedMockK
    lateinit var reminderDays: Preference<Int>

    private lateinit var reminderScheduleProvider: ReminderScheduleProvider
    private lateinit var reminderDaysList: List<Boolean>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        reminderScheduleProvider = ReminderScheduleProvider(dateHelper, exerciseSettingsRepository)
        every { isReminderEnabled.value } returns true
        every { reminderDays.value } answers { ReminderDaysEncoderDecoder.encodeDays(reminderDaysList) }
        every { exerciseSettingsRepository.reminderDays } returns reminderDays
        every { exerciseSettingsRepository.isReminderEnabled } returns isReminderEnabled
    }

    @Test
    fun `should isNeedToShowReminderToday return true if reminder enabled and today enabled in settings`() {
        // Arrange
        every { dateHelper.now() } returns Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Novosibirsk")
            time = Date(1612104504071) // 31 Jan 2021, 21:49, Sunday
        }
        reminderDaysList = listOf(false, false, false, false, false, false, true)
        // Act
        val isNeedToShowReminderToday = reminderScheduleProvider.isNeedToShowReminderToday()
        // Assert
        isNeedToShowReminderToday `should be equal to` true
    }

    @Test
    fun `should isNeedToShowReminderToday return false if reminder enabled and today disabled in settings`() {
        // Arrange
        every { dateHelper.now() } returns Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Novosibirsk")
            time = Date(1611982800000) // 30 Jan 2021, 12:00, Saturday
        }
        reminderDaysList = listOf(true, true, true, true, true, false, true)
        // Act
        val isNeedToShowReminderToday = reminderScheduleProvider.isNeedToShowReminderToday()
        // Assert
        isNeedToShowReminderToday `should be equal to` false
    }
}
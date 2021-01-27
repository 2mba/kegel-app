package org.tumba.kegel_app.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.worker.NotifierWorkerManager
import javax.inject.Inject

class SettingsInteractor @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val notifierWorkerManager: NotifierWorkerManager
) {

    fun observeReminderEnabled(): Flow<Boolean> {
        return exerciseSettingsRepository.isReminderEnabled.asFlow()
    }

    fun getReminderDays(): List<Boolean> {
        val encodedDays = exerciseSettingsRepository.reminderDays.value
        return (0 until ReminderDaysEncoderDecoder.DAYS_IN_WEEK)
            .map { day -> ReminderDaysEncoderDecoder.decodeWeekDay(encodedDays, day) }
            .toList()
    }

    suspend fun setReminderDays(days: List<Boolean>) {
        withContext(Dispatchers.Default) {
            exerciseSettingsRepository.reminderDays.value = ReminderDaysEncoderDecoder.encodeDays(days)
            notifierWorkerManager.rescheduleReminderWorker()
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        exerciseSettingsRepository.isReminderEnabled.value = enabled
        notifierWorkerManager.rescheduleReminderWorker()
    }
}

object ReminderDaysEncoderDecoder {

    const val DAYS_IN_WEEK = 7

    fun decodeWeekDay(encodedDays: Int, dayIdx: Int): Boolean = encodedDays.and(getDayBit(dayIdx)) > 0

    fun encodeDays(days: List<Boolean>): Int {
        var encodedDays = 0
        days.forEachIndexed { index, day ->
            if (day) {
                encodedDays = encodedDays.or(getDayBit(index))
            }
        }
        return encodedDays
    }

    fun getDayBit(day: Int): Int = 1.shl(DAYS_IN_WEEK - day - 1)

}
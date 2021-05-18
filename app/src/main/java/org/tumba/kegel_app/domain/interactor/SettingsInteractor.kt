package org.tumba.kegel_app.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.tumba.kegel_app.core.system.NightModeManager
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.worker.ReminderWorkerManager
import javax.inject.Inject

class SettingsInteractor @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val reminderWorkerManager: ReminderWorkerManager,
    private val nightModeManager: NightModeManager
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
            reminderWorkerManager.rescheduleReminderWorker()
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        exerciseSettingsRepository.isReminderEnabled.value = enabled
        reminderWorkerManager.rescheduleReminderWorker()
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        withContext(Dispatchers.Default) {
            exerciseSettingsRepository.reminderHour.value = hour
            exerciseSettingsRepository.reminderMinute.value = minute
            reminderWorkerManager.rescheduleReminderWorker()
        }
    }

    fun getReminderTime(): ReminderTime {
        return ReminderTime(
            exerciseSettingsRepository.reminderHour.value,
            exerciseSettingsRepository.reminderMinute.value
        )
    }

    fun observeReminderTime(): Flow<ReminderTime> {
        return combine(
            exerciseSettingsRepository.reminderHour.asFlow(),
            exerciseSettingsRepository.reminderMinute.asFlow(),
        ) { hour, minute -> ReminderTime(hour, minute) }
    }

    fun observeLevel(): Flow<Int> {
        return exerciseSettingsRepository.exerciseLevel.asFlow()
    }

    suspend fun setLevel(level: Int) {
        withContext(Dispatchers.Default) {
            exerciseSettingsRepository.exerciseLevel.value = level
        }
    }

    fun setNightMode(mode: Int) {
        nightModeManager.setNightMode(mode)
        exerciseSettingsRepository.nightMode.value = mode
    }

    fun getNightMode(): Int {
        return nightModeManager.getNightMode()
    }

    fun restoreNightMode() {
        nightModeManager.setNightMode(exerciseSettingsRepository.nightMode.value)
    }

    fun observeSoundVolume(): Flow<Float> {
        return exerciseSettingsRepository.soundVolume.asFlow()
    }

    fun setSoundVolume(value: Float) {
        exerciseSettingsRepository.soundVolume.value = value
    }

    fun observeSoundPack(): Flow<Int> {
        return exerciseSettingsRepository.soundPack.asFlow()
    }

    suspend fun setSoundPack(id: Int) {
        withContext(Dispatchers.Default) {
            exerciseSettingsRepository.soundPack.value = id
        }
    }

    data class ReminderTime(val hour: Int, val minute: Int)
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

    private fun getDayBit(day: Int): Int = 1.shl(DAYS_IN_WEEK - day - 1)

}
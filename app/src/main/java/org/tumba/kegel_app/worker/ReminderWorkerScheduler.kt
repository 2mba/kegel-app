package org.tumba.kegel_app.worker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderWorkerScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun scheduleNextWork() {
        val dailyWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(calculateDelayOfWorkerLaunch(), TimeUnit.MILLISECONDS)
            .addTag(ReminderWorker.WORKER_TAG)
            .build()
        workManager.enqueue(dailyWorkRequest)
    }

    private fun calculateDelayOfWorkerLaunch(): Long {
        val currentDate = Calendar.getInstance()
        return getNextWorkerLaunchTime().timeInMillis - currentDate.timeInMillis
    }

    private fun getNextWorkerLaunchTime(): Calendar {
        val time = SettingsInteractor.ReminderTime(
            exerciseSettingsRepository.reminderHour.value,
            exerciseSettingsRepository.reminderMinute.value
        )
        val now = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            if (before(now)) {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }
    }
}
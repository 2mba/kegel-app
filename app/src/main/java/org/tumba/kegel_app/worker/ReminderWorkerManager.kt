package org.tumba.kegel_app.worker

import androidx.work.WorkManager
import androidx.work.await
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class ReminderWorkerManager @Inject constructor(
    private val workManager: WorkManager,
    private val reminderWorkerScheduler: ReminderWorkerScheduler,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun onNotifierWorkerCompleted() {
        reminderWorkerScheduler.scheduleNextWork()
    }

    suspend fun rescheduleReminderWorker() {
        cancelReminderWorker()
        if (exerciseSettingsRepository.isReminderEnabled.value) {
            reminderWorkerScheduler.scheduleNextWork()
        }
    }

    private suspend fun cancelReminderWorker() {
        workManager.cancelAllWorkByTag(ReminderWorker.WORKER_TAG).await()
    }
}
package org.tumba.kegel_app.worker

import androidx.work.WorkManager
import androidx.work.await
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class NotifierWorkerManager @Inject constructor(
    private val workManager: WorkManager,
    private val notifierWorkerScheduler: NotifierWorkerScheduler,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun onNotifierWorkerCompleted() {
        notifierWorkerScheduler.scheduleNextWork()
    }

    suspend fun rescheduleReminderWorker() {
        cancelReminderWorker()
        if (exerciseSettingsRepository.isReminderEnabled.value) {
            notifierWorkerScheduler.scheduleNextWork()
        }
    }

    private suspend fun cancelReminderWorker() {
        workManager.cancelAllWorkByTag(NotifierWorker.WORKER_TAG).await()
    }
}
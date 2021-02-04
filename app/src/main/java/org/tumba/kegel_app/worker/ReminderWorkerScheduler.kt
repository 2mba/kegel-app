package org.tumba.kegel_app.worker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.tumba.kegel_app.analytics.WorkerTracker
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderWorkerScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val tracker: WorkerTracker
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
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }
    }
}
package org.tumba.kegel_app.worker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotifierWorkerScheduler @Inject constructor(
    private val workManager: WorkManager
) {

    fun scheduleNextWork() {
        val dailyWorkRequest = OneTimeWorkRequestBuilder<NotifierWorker>()
            .setInitialDelay(calculateDelayOfWorkerLaunch(), TimeUnit.MILLISECONDS)
            .addTag(NotifierWorker.WORKER_TAG)
            .build()
        workManager.enqueue(dailyWorkRequest)
    }

    private fun calculateDelayOfWorkerLaunch(): Long {
        val currentDate = Calendar.getInstance()
        return getNextWorkerLaunchTime().timeInMillis - currentDate.timeInMillis
    }

    private fun getNextWorkerLaunchTime(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.HOUR_OF_DAY, 24)
            }
        }
    }
}
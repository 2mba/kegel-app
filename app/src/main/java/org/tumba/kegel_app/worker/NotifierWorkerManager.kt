package org.tumba.kegel_app.worker

import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotifierWorkerManager @Inject constructor(
    private val workManager: WorkManager,
    private val notifierWorkerScheduler: NotifierWorkerScheduler
) {

    fun onStartApp() {
        rescheduleNotifierWorker()
    }

    fun onNotifierWorkerCompleted() {
        notifierWorkerScheduler.scheduleNextWork()
    }

    fun rescheduleNotifierWorker() {
        GlobalScope.launch {
            workManager.cancelAllWorkByTag(NotifierWorker.WORKER_TAG).await()
            notifierWorkerScheduler.scheduleNextWork()
        }
    }
}
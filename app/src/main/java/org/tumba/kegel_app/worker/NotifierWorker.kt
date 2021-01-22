package org.tumba.kegel_app.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.tumba.kegel_app.analytics.WorkerTracker
import org.tumba.kegel_app.di.appComponent
import javax.inject.Inject

class NotifierWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    @Inject
    lateinit var notifierWorkerManager: NotifierWorkerManager

    @Inject
    lateinit var tracker: WorkerTracker

    init {
        appComponent.inject(this)
        tracker.trackInitWorker()
    }

    override fun doWork(): Result {
        tracker.trackDoWork()
        showNotification()
        notifierWorkerManager.onNotifierWorkerCompleted()
        return Result.success()
    }

    private fun showNotification() {
        ExerciseNotifierNotificationManager(appContext).showExerciseNotifierNotification()
    }

    companion object {
        const val WORKER_TAG = "NotifierWorker.Tag"
    }
}


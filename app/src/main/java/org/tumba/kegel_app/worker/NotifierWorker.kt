package org.tumba.kegel_app.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.tumba.kegel_app.di.appComponent
import javax.inject.Inject

class NotifierWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    @Inject
    lateinit var notifierWorkerManager: NotifierWorkerManager

    init {
        appComponent.inject(this)
    }

    override fun doWork(): Result {
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


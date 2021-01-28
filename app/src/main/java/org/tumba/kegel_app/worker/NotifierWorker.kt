package org.tumba.kegel_app.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.tumba.kegel_app.analytics.WorkerTracker
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.domain.interactor.ReminderDaysEncoderDecoder
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import java.util.*
import javax.inject.Inject

class NotifierWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    @Inject
    lateinit var notifierWorkerManager: NotifierWorkerManager

    @Inject
    lateinit var tracker: WorkerTracker

    @Inject
    lateinit var exerciseSettingsRepository: ExerciseSettingsRepository

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
        if (isNeedToShowReminderToday()) {
            ExerciseNotifierNotificationManager(appContext).showExerciseNotifierNotification()
        }
    }

    private fun isNeedToShowReminderToday(): Boolean {
        val dayOfWeekFromCalendar = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = (Calendar.MONDAY - dayOfWeekFromCalendar + DAYS_IN_WEEK) % DAYS_IN_WEEK
        return exerciseSettingsRepository.isReminderEnabled.value &&
                ReminderDaysEncoderDecoder.decodeWeekDay(exerciseSettingsRepository.reminderDays.value, dayOfWeek)
    }

    companion object {
        const val WORKER_TAG = "NotifierWorker.Tag"
        private const val DAYS_IN_WEEK = 7
    }
}


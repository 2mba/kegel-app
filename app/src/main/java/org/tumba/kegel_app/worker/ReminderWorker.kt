package org.tumba.kegel_app.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.tumba.kegel_app.analytics.WorkerTracker
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.domain.interactor.ReminderDaysEncoderDecoder
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.utils.DateHelper
import java.util.*
import javax.inject.Inject

class ReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    @Inject
    lateinit var reminderWorkerManager: ReminderWorkerManager

    @Inject
    lateinit var tracker: WorkerTracker

    @Inject
    lateinit var reminderScheduleProvider: ReminderScheduleProvider

    @Inject
    lateinit var resourceProvider: ResourceProvider

    init {
        appComponent.inject(this)
    }

    override fun doWork(): Result {
        showNotification()
        reminderWorkerManager.onNotifierWorkerCompleted()
        return Result.success()
    }

    private fun showNotification() {
        if (reminderScheduleProvider.isNeedToShowReminderToday()) {
            ExerciseNotifierNotificationManager(appContext, resourceProvider).showExerciseNotifierNotification()
        }
    }

    companion object {
        const val WORKER_TAG = "ReminderWorker.Tag"
    }
}

class ReminderScheduleProvider @Inject constructor(
    private val dateHelper: DateHelper,
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun isNeedToShowReminderToday(): Boolean {
        val dayOfWeekFromCalendar = dateHelper.now().get(Calendar.DAY_OF_WEEK)
        val dayOfWeek = (dayOfWeekFromCalendar - Calendar.MONDAY + DAYS_IN_WEEK) % DAYS_IN_WEEK
        return exerciseSettingsRepository.isReminderEnabled.value &&
                 ReminderDaysEncoderDecoder.decodeWeekDay(exerciseSettingsRepository.reminderDays.value, dayOfWeek)
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}


package org.tumba.kegel_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.tumba.kegel_app.analytics.ExerciseNotificationTracker
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.service.ExerciseServiceNotificationProvider.Companion.ACTION_PAUSE_EXERCISE
import org.tumba.kegel_app.service.ExerciseServiceNotificationProvider.Companion.ACTION_RESUME_EXERCISE
import org.tumba.kegel_app.service.ExerciseServiceNotificationProvider.Companion.ACTION_STOP_EXERCISE
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ExerciseAndroidService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    @Inject
    lateinit var tracker: ExerciseNotificationTracker

    @Inject
    lateinit var exerciseInteractor: ExerciseInteractor

    @Inject
    lateinit var settingsRepository: ExerciseSettingsRepository

    @Inject
    lateinit var notificationProvider: ExerciseServiceNotificationProvider

    private var isForegroundService = true
    private var exerciseObserveJob: Job? = null
    private var isNotificationsCleared = false

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        tracker.trackCreated()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_RESUME_EXERCISE -> resumeExercise()
            ACTION_PAUSE_EXERCISE -> pauseExercise()
            ACTION_STOP_EXERCISE -> stopExercise()
            ACTION_SHOW_EXERCISE_STATUS -> showExerciseStatus()
            ACTION_CLEAR_NOTIFICATION -> clearNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun showExerciseStatus() {
        launch {
            if (exerciseInteractor.getExercise() == null) {
                stopForeground(true)
            }
        }
        observeExercise()
    }

    private fun observeExercise() {
        isNotificationsCleared = false
        exerciseObserveJob?.cancel()
        exerciseObserveJob = launch {
            exerciseInteractor.observeExerciseState().collect {
                if (!isNotificationsCleared) {
                    onExerciseEvents(it)
                }
            }
        }
    }

    private fun onExerciseEvents(state: ExerciseState) {
        if (state is ExerciseState.Finish) {
            if (isForegroundService) {
                stopForeground(false)
                isForegroundService = false
            }
        }
        if (settingsRepository.isNotificationEnabled()) {
            showNotification(state, isForegroundService)
        }
    }

    private fun resumeExercise() {
        tracker.trackPlay()
        launch {
            exerciseInteractor.resumeExercise()
        }
    }

    private fun pauseExercise() {
        tracker.trackPause()
        launch {
            exerciseInteractor.pauseExercise()
        }
    }

    private fun stopExercise() {
        tracker.trackStop()
        launch {
            exerciseInteractor.stopExercise()
        }
    }

    private fun showNotification(exerciseState: ExerciseState, isForeground: Boolean) {
        val notification = notificationProvider.createNotification(exerciseState)

        if (isForeground) {
            startForeground(ONGOING_EXERCISE_NOTIFICATION_ID, notification)
            isForegroundService = true
        } else {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(ONGOING_EXERCISE_NOTIFICATION_ID, notification)
        }
    }

    private fun clearNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(ONGOING_EXERCISE_NOTIFICATION_ID)
        stopForeground(true)
        exerciseObserveJob?.cancel()
        isNotificationsCleared = true
    }

    companion object {
        const val ACTION_SHOW_EXERCISE_STATUS = "ACTION_SHOW_EXERCISE_STATUS"
        const val ACTION_CLEAR_NOTIFICATION = "ACTION_CLEAR_NOTIFICATION"
        private const val ONGOING_EXERCISE_NOTIFICATION_ID = 1
    }
}
package org.tumba.kegel_app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.tumba.kegel_app.di.Di
import org.tumba.kegel_app.domain.ExerciseInteractor
import org.tumba.kegel_app.domain.ExerciseState
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ExerciseAndroidService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    @Inject
    lateinit var exerciseInteractor: ExerciseInteractor
    @Inject
    lateinit var notificationProvider: ExerciseServiceNotificationProvider

    private var isForegroundService = true

    override fun onCreate() {
        super.onCreate()
        Di.appComponent.inject(this)
        launch {
            if (exerciseInteractor.getExercise() == null) {
                stopForeground(true)
            }
        }
        observeExercise()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ExerciseServiceNotificationProvider.ACTION_RESUME_EXERCISE -> resumeExercise()
            ExerciseServiceNotificationProvider.ACTION_PAUSE_EXERCISE -> pauseExercise()
            ExerciseServiceNotificationProvider.ACTION_STOP_EXERCISE -> stopExercise()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun observeExercise() {
        launch {
            exerciseInteractor.observeExerciseState().collect { onExerciseEvents(it) }
        }
    }

    private fun onExerciseEvents(state: ExerciseState) {
        if (state is ExerciseState.Finish) {
            if (isForegroundService) {
                stopForeground(false)
                isForegroundService = false
            }
        }
        showNotification(state, isForegroundService)
    }

    private fun resumeExercise() {
        launch {
            exerciseInteractor.resumeExercise()
        }
    }

    private fun pauseExercise() {
        launch {
            exerciseInteractor.pauseExercise()
        }
    }

    private fun stopExercise() {
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

    companion object {
        private const val ONGOING_EXERCISE_NOTIFICATION_ID = 1
    }
}
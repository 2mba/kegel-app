package org.tumba.kegel_app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.R
import org.tumba.kegel_app.domain.ExerciseState

class ExerciseServiceNotificationProvider(private val context: Context) {

    init {
        createNotificationChannel()
    }

    fun createNotification(exerciseState: ExerciseState): Notification {
        val pendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }
        return NotificationCompat.Builder(context, EXERCISES_CHANNEL_ID)
            .setContentTitle("Упражнение")
            .setContentText(buildNotificationContentText(exerciseState))
            .setSmallIcon(R.drawable.ic_autorenew)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addActions(exerciseState)
            .setNotificationSilent()
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun buildNotificationContentText(exerciseState: ExerciseState): String {
        var remainSeconds: Long? = null
        var duration: Long? = null
        when (exerciseState){
            is ExerciseState.Preparation -> {
                remainSeconds = exerciseState.remainSeconds
                duration = exerciseState.exerciseDurationSeconds
            }
            is ExerciseState.Holding -> {
                remainSeconds = exerciseState.remainSeconds
                duration = exerciseState.exerciseDurationSeconds
            }
            is ExerciseState.Relax -> {
                remainSeconds = exerciseState.remainSeconds
                duration = exerciseState.exerciseDurationSeconds
            }
        }
        return buildString {
            append(exerciseState::class.java.simpleName).append(" ")
            if (remainSeconds != null && duration != null) {
                append(DateUtils.formatElapsedTime(duration - remainSeconds))
                append("/").append(DateUtils.formatElapsedTime(duration))
            }
        }
    }


    private fun NotificationCompat.Builder.addActions(
        exerciseState: ExerciseState
    ): NotificationCompat.Builder {
        val isPaused = exerciseState is ExerciseState.Pause
        val isInProgress = exerciseState != ExerciseState.Finish &&
                exerciseState !is ExerciseState.Pause &&
                exerciseState !is ExerciseState.NotStarted
        if (isPaused) {
            addAction(
                NotificationCompat.Action(null, "Продолжить", getExerciseActionPendingIntent(ACTION_RESUME_EXERCISE))
            )
        } else if (isInProgress) {
            addAction(
                NotificationCompat.Action(null, "Пауза", getExerciseActionPendingIntent(ACTION_PAUSE_EXERCISE))
            )
        }
        if (isInProgress || isPaused) {
            addAction(
                NotificationCompat.Action(null, "Остановить", getExerciseActionPendingIntent(ACTION_STOP_EXERCISE))
            )
        }
        return this
    }

    private fun getExerciseActionPendingIntent(action: String): PendingIntent {
        return Intent(context, ExerciseAndroidService::class.java).let { notificationIntent ->
            notificationIntent.action = action
            PendingIntent.getService(context, ACTION_REQUEST_CODE, notificationIntent, 0)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(EXERCISES_CHANNEL_ID, "Exercises", importance).apply {
                description = "On background run exercise notifications"
            }
            val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        const val ACTION_RESUME_EXERCISE = "RESUME_EXERCISE_ACTION"
        const val ACTION_PAUSE_EXERCISE = "PAUSE_EXERCISE_ACTION"
        const val ACTION_STOP_EXERCISE = "STOP_EXERCISE_ACTION"
        private const val EXERCISES_CHANNEL_ID = "ExercisesChannel"
        private const val ACTION_REQUEST_CODE = 1000
    }
}
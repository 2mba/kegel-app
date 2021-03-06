package org.tumba.kegel_app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.R
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.notification.AppNotificationConstants
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import javax.inject.Inject

class ExerciseServiceNotificationProvider @Inject constructor(
    private val context: Context,
    private var exerciseNameProvider: ExerciseNameProvider
) {

    init {
        createNotificationChannel()
    }

    fun createNotification(exerciseState: ExerciseState): Notification {
        val pendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }
        return NotificationCompat.Builder(context, AppNotificationConstants.EXERCISES_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.exercise_notification_title))
            .setContentText(buildNotificationContentText(exerciseState))
            .setSmallIcon(R.drawable.ic_app_icon_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.colorAccent, null))
            .addActions(exerciseState)
            .setNotificationSilent()
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun buildNotificationContentText(exerciseState: ExerciseState): String {
        val remainSeconds = (exerciseState as? ExerciseState.SingleExercise)?.singleExerciseInfo?.remainSeconds
        val duration = (exerciseState as? ExerciseState.SingleExercise)?.exerciseInfo?.remainSeconds
        return buildString {
            if (exerciseState is ExerciseState.Pause) {
                append(EMOJI_WALKING_MAN)
            } else {
                append(EMOJI_RUNNING_MAN)
            }
            append(exerciseNameProvider.exerciseName(exerciseState))

            if (remainSeconds != null && duration != null) {
                append(" ").append(EMOJI_STOPWATCH)
                append(DateUtils.formatElapsedTime(remainSeconds))
                append(" ").append(EMOJI_TIMER)
                append(DateUtils.formatElapsedTime(duration))
            }
        }
    }


    private fun NotificationCompat.Builder.addActions(
        exerciseState: ExerciseState
    ): NotificationCompat.Builder {
        val isPaused = exerciseState is ExerciseState.Pause
        val isInProgress = exerciseState !is ExerciseState.Finish &&
                exerciseState !is ExerciseState.Pause &&
                exerciseState !is ExerciseState.NotStarted
        when {
            isPaused -> {
                addAction(
                    NotificationCompat.Action(
                        null,
                        context.getString(R.string.exercise_notification_action_resume),
                        getExerciseActionPendingIntent(ACTION_RESUME_EXERCISE)
                    )
                )
            }
            isInProgress -> {
                addAction(
                    NotificationCompat.Action(
                        null,
                        context.getString(R.string.exercise_notification_action_pause),
                        getExerciseActionPendingIntent(ACTION_PAUSE_EXERCISE)
                    )
                )
            }
        }
        if (isInProgress || isPaused) {
            addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.exercise_notification_action_stop),
                    getExerciseActionPendingIntent(ACTION_STOP_EXERCISE)
                )
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
            val channel = NotificationChannel(
                AppNotificationConstants.EXERCISES_CHANNEL_ID,
                EXERCISES_CHANNEL_NAME,
                importance
            ).apply {
                description = "On background run exercise notifications"
            }
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_RESUME_EXERCISE = "RESUME_EXERCISE_ACTION"
        const val ACTION_PAUSE_EXERCISE = "PAUSE_EXERCISE_ACTION"
        const val ACTION_STOP_EXERCISE = "STOP_EXERCISE_ACTION"
        private const val EXERCISES_CHANNEL_NAME = "Exercises"
        private const val ACTION_REQUEST_CODE = 1000
        private const val EMOJI_WALKING_MAN = "\uD83D\uDEB6"
        private const val EMOJI_RUNNING_MAN = "\uD83C\uDFC3️"
        private const val EMOJI_TIMER = "⌛"
        private const val EMOJI_STOPWATCH = "⏱️"
    }
}
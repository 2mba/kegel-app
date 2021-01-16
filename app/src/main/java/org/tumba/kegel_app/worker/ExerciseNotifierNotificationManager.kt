package org.tumba.kegel_app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.tumba.kegel_app.R
import javax.inject.Inject

class ExerciseNotifierNotificationManager @Inject constructor(
    private val context: Context
) {

    init {
        createNotificationChannel()
    }

    fun showExerciseNotifierNotification() {
        val notification = NotificationCompat.Builder(context, EXERCISE_NOTIFIER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fit_24)
            .setContentText("It's time to workout") // TODO localize
            .build()
        NotificationManagerCompat.from(context)
            .notify(EXERCISE_NOTIFIER_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EXERCISE_NOTIFIER_NOTIFICATION_CHANNEL_ID,
                "Notify", // TODO localize
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily exercise notifications" // TODO localize
            }
            val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val EXERCISE_NOTIFIER_ID = 100
        private const val EXERCISE_NOTIFIER_NOTIFICATION_CHANNEL_ID = "EXERCISE_NOTIFIER_NOTIFICATION_CHANNEL_ID"
    }
}
package org.tumba.kegel_app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.notification.AppNotificationConstants
import javax.inject.Inject

class ExerciseNotifierNotificationManager @Inject constructor(
    private val context: Context,
    private val resourceProvider: ResourceProvider
) {

    init {
        createNotificationChannel()
    }

    fun showExerciseNotifierNotification() {
        val notification = NotificationCompat.Builder(context, AppNotificationConstants.REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fit_24)
            .setContentText(resourceProvider.getString(R.string.notification_reminder_title))
            .build()
        NotificationManagerCompat.from(context)
            .notify(AppNotificationConstants.REMINDER_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppNotificationConstants.REMINDER_CHANNEL_ID,
                resourceProvider.getString(R.string.notification_reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = resourceProvider.getString(R.string.notification_reminder_channel_description)
            }
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }
}
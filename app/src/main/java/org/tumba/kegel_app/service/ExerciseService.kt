package org.tumba.kegel_app.service

import android.content.Context
import android.content.Intent
import timber.log.Timber

interface ExerciseService {

    fun startService()

    fun stopService()

    fun clearNotification()
}

class ExerciseServiceProxy(private val context: Context) : ExerciseService {

    override fun startService() {
        val intent = Intent(context, ExerciseAndroidService::class.java).apply {
            action = ExerciseAndroidService.ACTION_SHOW_EXERCISE_STATUS
        }
        context.startService(intent)
    }

    override fun stopService() {
        context.stopService(Intent(context, ExerciseAndroidService::class.java))
    }

    override fun clearNotification() {
        val intent = Intent(context, ExerciseAndroidService::class.java).apply {
            action = ExerciseAndroidService.ACTION_CLEAR_NOTIFICATION
        }
        try {
            context.startService(intent)
        } catch (err: Throwable) {
            Timber.e(err, "Unable to start service")
        }
    }
}
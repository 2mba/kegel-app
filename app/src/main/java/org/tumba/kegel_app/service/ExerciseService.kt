package org.tumba.kegel_app.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

interface ExerciseService {

    fun startService()

    fun stopService()
}

class ExerciseServiceProxy(private val context: Context) : ExerciseService {

    override fun startService() {
        val intent = Intent(context, ExerciseAndroidService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopService() {
        context.stopService(Intent(context, ExerciseAndroidService::class.java))
    }
}
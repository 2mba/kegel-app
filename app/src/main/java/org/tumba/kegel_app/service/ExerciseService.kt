package org.tumba.kegel_app.service

import android.content.Context
import android.content.Intent

interface ExerciseService {

    fun startService()

    fun stopService()
}

class ExerciseServiceProxy(private val context: Context) : ExerciseService {

    override fun startService() {
        val intent = Intent(context, ExerciseAndroidService::class.java)
        context.startService(intent)
    }

    override fun stopService() {
        context.stopService(Intent(context, ExerciseAndroidService::class.java))
    }
}
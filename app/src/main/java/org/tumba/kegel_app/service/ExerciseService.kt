package org.tumba.kegel_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.R
import org.tumba.kegel_app.domain.ExerciseEvent
import org.tumba.kegel_app.utils.InjectorUtils
import kotlin.coroutines.CoroutineContext

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

class ExerciseAndroidService : Service(), CoroutineScope{

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val exerciseInteractor by lazy {
        InjectorUtils.provideExerciseInteractor(this)
    }

    override fun onCreate() {
        super.onCreate()
        showNotification(text = "-")
        observeExercise()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    private fun observeExercise() {
        launch {
            exerciseInteractor.observeExerciseEvents().collect { onExerciseEvents(it) }
        }
    }

    private fun onExerciseEvents(event: ExerciseEvent) {
        showNotification(event::class.simpleName.orEmpty())
    }

    private fun showNotification(text: String) {
        createNotificationChannel()
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        val notification = NotificationCompat.Builder(this, EXERCISES_CHANNEL_ID)
            .setContentTitle("Упражнение")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_autorenew)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setNotificationSilent()
            .setContentIntent(pendingIntent)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(EXERCISES_CHANNEL_ID, "Exercises", importance)
            mChannel.description = "On background run exercise notifications"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 1
        private const val EXERCISES_CHANNEL_ID = "ExercisesChannel"
    }
}




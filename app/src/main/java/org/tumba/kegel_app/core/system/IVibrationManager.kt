package org.tumba.kegel_app.core.system

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject


interface IVibrationManager {

    fun vibrate(duration: Long)

    fun vibrate(strength: Strength) {
        vibrate(strength.durationMillis)
    }

    enum class Strength(val durationMillis: Long) {
        Low(100),
        Medium(200),
        High(500)
    }
}

class VibrationManager @Inject constructor(private val context: Context) : IVibrationManager {

    @Suppress("DEPRECATION")
    override fun vibrate(duration: Long) {
        val v = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v?.vibrate(duration)
        }
    }
}
package org.tumba.kegel_app.core.system

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import org.tumba.kegel_app.R
import javax.inject.Inject

interface SoundManager {

    fun build()

    fun release()

    fun play(volume: Float)
}

class SoundManagerImpl @Inject constructor(
    private val context: Context
) : SoundManager {

    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private var sounds: SoundPool? = null
    private var soundBell: Int? = null

    override fun build() {
        sounds = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(2)
            .build()
        soundBell = sounds?.load(context, R.raw.bell, 1)
    }

    override fun release() {
        sounds?.release()
        soundBell = null
    }

    override fun play(volume: Float) {
        soundBell?.let { sound ->
            val volumeNormalized = volume.coerceIn(0f, 1f)
            sounds?.play(sound, volumeNormalized, volumeNormalized, 0, 0, 1f)
        }
    }
}

package org.tumba.kegel_app.core.system

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import org.tumba.kegel_app.sound.SoundPackManager
import javax.inject.Inject

interface SoundManager {

    fun build()

    fun release()

    fun play(volume: Float, soundId: Int)
}

class SoundManagerImpl @Inject constructor(
    private val context: Context,
    private val soundPackManager: SoundPackManager
) : SoundManager {

    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private var soundPool: SoundPool? = null
    private var soundIds = mutableMapOf<Int, Int>()

    override fun build() {
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(2)
            .build()

        val loadedSoundIds = mutableSetOf<Int>()
        soundPackManager.getAllPacks().forEach { pack ->
            loadSound(loadedSoundIds, pack.relaxSoundId)
            loadSound(loadedSoundIds, pack.holdSoundId)
        }
    }

    private fun loadSound(loadedSoundIds: MutableSet<Int>, rawSoundId: Int) {
        if (!loadedSoundIds.contains(rawSoundId)) {
            loadedSoundIds.add(rawSoundId)
            soundPool?.load(context, rawSoundId, 1)?.let { soundId ->
                soundIds[rawSoundId] = soundId
            }
        }
    }

    override fun release() {
        soundPool?.release()
        soundIds.clear()
    }

    override fun play(volume: Float, soundId: Int) {
        soundIds[soundId]?.let { sound ->
            val volumeNormalized = volume.coerceIn(0f, 1f)
            soundPool?.play(sound, volumeNormalized, volumeNormalized, 0, 0, 1f)
        }
    }
}

package org.tumba.kegel_app.sound

import org.tumba.kegel_app.R
import org.tumba.kegel_app.core.system.ResourceProvider
import javax.inject.Inject

class SoundPackManager @Inject constructor(private val resourceProvider: ResourceProvider) {

    private val namesToPacks = listOf(
        R.string.screen_settings_sound_pack_bell_1 to singleSoundPack(R.raw.bell1),
        R.string.screen_settings_sound_pack_bell_2 to singleSoundPack(R.raw.bell2),
        R.string.screen_settings_sound_pack_tweet to singleSoundPack(R.raw.tweet),
        R.string.screen_settings_sound_pack_clap to singleSoundPack(R.raw.clap),
        R.string.screen_settings_sound_pack_music_1 to singleSoundPack(R.raw.musical_instrument1),
        R.string.screen_settings_sound_pack_music_2 to singleSoundPack(R.raw.musical_instrument2),
    )

    fun getPack(id: Int): SoundPack? = namesToPacks.getOrNull(id)?.second

    fun getAllPacks(): List<SoundPack> = namesToPacks.map { it.second }

    fun getAllPackNames(): List<String> = namesToPacks.map { resourceProvider.getString(it.first) }
}

private fun singleSoundPack(soundId: Int): SoundPack {
    return SoundPack(
        preparationSoundId = soundId,
        holdSoundId = soundId,
        relaxSoundId = soundId,
    )
}
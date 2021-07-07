package org.tumba.kegel_app.domain

import androidx.annotation.RawRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tumba.kegel_app.core.system.SoundManager
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.sound.SoundPack
import org.tumba.kegel_app.sound.SoundPackManager
import javax.inject.Inject

interface ExerciseEffectsHandler {

    fun onStartExercise(state: Flow<ExerciseState>)
}

class ExerciseEffectsHandlerImpl @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val settingsInteractor: SettingsInteractor,
    private val vibrationManager: VibrationManager,
    private val soundManager: SoundManager,
    soundPackManager: SoundPackManager
) : ExerciseEffectsHandler {

    private val coroutineScope = CoroutineScope(GlobalScope.coroutineContext + Dispatchers.Main)
    private var soundVolume = 0f
    private var soundPack: SoundPack? = null
    private val allSoundPacks = soundPackManager.getAllPacks()

    override fun onStartExercise(state: Flow<ExerciseState>) {
        soundManager.build()
        observeSoundVolume()
        observeSoundPack()
        coroutineScope.launch { handleExerciseKindChanges(state) }
        coroutineScope.launch { handleExerciseFinish(state) }
    }

    private fun observeSoundVolume() {
        coroutineScope.launch {
            exerciseSettingsRepository.soundVolume
                .asFlow()
                .flowOn(Dispatchers.Default)
                .collect { soundVolume = it }
        }
    }

    private fun observeSoundPack() {
        coroutineScope.launch {
            exerciseSettingsRepository.soundPack
                .asFlow()
                .flowOn(Dispatchers.Default)
                .collect { soundPack = allSoundPacks.getOrNull(it) }
        }
    }

    private suspend fun handleExerciseKindChanges(state: Flow<ExerciseState>) {
        state.distinctUntilChanged { exercise1, exercise2 -> exercise1.javaClass == exercise2.javaClass }
            .flowOn(Dispatchers.Default)
            .collect { exerciseState ->
                vibrate(exerciseState)
                playSound(exerciseState)
            }
    }

    private suspend fun handleExerciseFinish(state: Flow<ExerciseState>) {
        state.filterIsInstance<ExerciseState.Finish>()
            .collect { soundManager.release() }
    }

    private fun vibrate(state: ExerciseState) {
        if (state is ExerciseState.Pause || state is ExerciseState.Preparation) {
            return
        }
        if (exerciseSettingsRepository.isVibrationEnabled.value) {
            vibrationManager.vibrate(VibrationManager.Strength.Medium)
        }
    }

    private suspend fun playSound(state: ExerciseState) {
        if (state is ExerciseState.Pause || state is ExerciseState.Preparation) {
            return
        }
        val soundId = getSoundId(state)
        if (settingsInteractor.observeSoundEnabled().first() && soundId != null) {
            soundManager.play(exerciseSettingsRepository.soundVolume.value, soundId)
        }
    }

    @RawRes
    private fun getSoundId(state: ExerciseState): Int? {
        return soundPack?.let { soundPack ->
            when (state) {
                is ExerciseState.Holding -> soundPack.holdSoundId
                is ExerciseState.Relax -> soundPack.relaxSoundId
                else -> null
            }
        }
    }
}

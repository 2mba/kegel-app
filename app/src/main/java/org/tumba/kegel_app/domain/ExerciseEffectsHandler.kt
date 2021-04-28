package org.tumba.kegel_app.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.tumba.kegel_app.core.system.SoundManager
import org.tumba.kegel_app.core.system.VibrationManager
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

interface ExerciseEffectsHandler {

    fun onStartExercise(state: Flow<ExerciseState>)
}

class ExerciseEffectsHandlerImpl @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val vibrationManager: VibrationManager,
    private val soundManager: SoundManager
) : ExerciseEffectsHandler {

    private val coroutineScope = CoroutineScope(GlobalScope.coroutineContext + Dispatchers.Main)

    override fun onStartExercise(state: Flow<ExerciseState>) {
        soundManager.build()
        coroutineScope.launch { handleExerciseKindChanges(state) }
        coroutineScope.launch { handleExerciseFinish(state) }
    }

    private suspend fun handleExerciseKindChanges(state: Flow<ExerciseState>) {
        state.filterIsInstance<ExerciseState.SingleExercise>()
            .filter { it !is ExerciseState.Pause && it !is ExerciseState.Preparation }
            .distinctUntilChanged { exercise1, exercise2 -> exercise1.javaClass == exercise2.javaClass }
            .flowOn(Dispatchers.Default)
            .collect {
                vibrate()
                playSound()
            }
    }

    private suspend fun handleExerciseFinish(state: Flow<ExerciseState>) {
        state.filterIsInstance<ExerciseState.Finish>()
            .collect { soundManager.release() }
    }

    private fun vibrate() {
        if (exerciseSettingsRepository.isVibrationEnabled.value) {
            vibrationManager.vibrate(VibrationManager.Strength.Medium)
        }
    }

    private fun playSound() {
        if (exerciseSettingsRepository.isSoundEnabled.value) {
            soundManager.play()
        }
    }
}

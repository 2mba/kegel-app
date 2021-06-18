package org.tumba.kegel_app.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import javax.inject.Inject

class CustomExerciseInteractor @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun observeTenseDuration(): Flow<Int> {
        return exerciseSettingsRepository.customExerciseTenseDuration.asFlow()
    }

    fun observeRelaxDuration(): Flow<Int> {
        return exerciseSettingsRepository.customExerciseRelaxDuration.asFlow()
    }

    fun observeRepeats(): Flow<Int> {
        return exerciseSettingsRepository.customExerciseRepeats.asFlow()
    }

    fun observeIsCustomExerciseConfigured(): Flow<Boolean> {
        return exerciseSettingsRepository.isCustomExerciseConfigured.asFlow()
    }

    suspend fun setTenseDuration(duration: Int) {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.isCustomExerciseConfigured.value = true
            exerciseSettingsRepository.customExerciseTenseDuration.value = duration
        }
    }

    suspend fun setRelaxDuration(duration: Int) {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.customExerciseRelaxDuration.value = duration
        }
    }

    suspend fun setRepeats(repeats: Int) {
        withContext(Dispatchers.IO) {
            exerciseSettingsRepository.customExerciseRepeats.value = repeats
        }
    }
}
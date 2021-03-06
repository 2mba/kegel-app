package org.tumba.kegel_app.domain

import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.min

class ExerciseProgram @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository
) {

    fun getConfig(): ExerciseConfig {
        val level = exerciseSettingsRepository.exerciseLevel.value
        val relaxSeconds =
            interpolateDuration(RELAX_TIME_MIN_SECONDS, RELAX_TIME_MAX_SECONDS, level, RELAX_TIME_COEFFICIENT)
        val holdingSeconds =
            interpolateDuration(HOLDING_TIME_MIN_SECONDS, HOLDING_TIME_MAX_SECONDS, level, HOLDING_TIME_COEFFICIENT)
        return ExerciseConfig(
            preparationDuration = Time(PREPARATION_TIME_SECONDS, TimeUnit.SECONDS),
            holdingDuration = Time(holdingSeconds, TimeUnit.SECONDS),
            relaxDuration = Time(relaxSeconds, TimeUnit.SECONDS),
            repeats = REPEATS,
            isPredefined = true
        )
    }

    private fun interpolateDuration(min: Long, max: Long, level: Int, coefficient: Double): Long {
        return min(min + ((level - 1) * coefficient).toLong(), max)
    }

    companion object {
        const val PREPARATION_TIME_SECONDS = 3L
        private const val HOLDING_TIME_MIN_SECONDS = 3L
        private const val HOLDING_TIME_MAX_SECONDS = 20L
        private const val HOLDING_TIME_COEFFICIENT = 1.0
        private const val RELAX_TIME_MIN_SECONDS = 3L
        private const val RELAX_TIME_MAX_SECONDS = 5L
        private const val RELAX_TIME_COEFFICIENT = 0.6
        private const val REPEATS = 10
    }
}
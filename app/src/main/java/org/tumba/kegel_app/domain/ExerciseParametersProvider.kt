package org.tumba.kegel_app.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.tumba.kegel_app.repository.ExerciseSettingsRepository
import org.tumba.kegel_app.utils.DateTimeHelper
import java.util.*
import javax.inject.Inject

class ExerciseParametersProvider @Inject constructor(
    private val exerciseSettingsRepository: ExerciseSettingsRepository,
    private val dateTimeHelper: DateTimeHelper
) {

    fun observeLevel(): Flow<Int> = exerciseSettingsRepository.exerciseLevel.asFlow()

    fun observeNumberOfCompletedExercises(): Flow<Int> = exerciseSettingsRepository.numberOfCompletedExercises.asFlow()

    fun observeExercisesDurationInSeconds(): Flow<Long> = exerciseSettingsRepository.exercisesDurationInSeconds.asFlow()

    fun observeDay(): Flow<Int> {
        return combine(
            exerciseSettingsRepository.exerciseDay.asFlow(),
            exerciseSettingsRepository.lastCompletedExerciseDate.asFlow()
        ) { day, lastCompletedExerciseDate ->
            if (lastCompletedExerciseDate == 0L) 1 else calcDay(lastCompletedExerciseDate, day)
        }
    }

    fun observeProgress(): Flow<Progress> {
        return observeDay().map { day ->
            val completedDays = exerciseSettingsRepository.exerciseDay.value
            Progress(
                completed = completedDays % 7,
                next = if (completedDays == day) null else (day - 1) % 7
            )
        }
    }

    private fun calcDay(lastCompletedExerciseDateRaw: Long, day: Int): Int {
        val lastCompletedExerciseDate = Calendar.getInstance().apply {
            time = Date(lastCompletedExerciseDateRaw)
        }
        val now = dateTimeHelper.now()
        return if (lastCompletedExerciseDateRaw != 0L && !isDatesHaveSameDays(lastCompletedExerciseDate, now)) {
            day + 1
        } else {
            day
        }
    }

    private fun isDatesHaveSameDays(date: Calendar, other: Calendar): Boolean {
        return date.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) &&
                date.get(Calendar.YEAR) == other.get(Calendar.YEAR)
    }

    class Progress(
        val completed: Int,
        val next: Int?
    )
}
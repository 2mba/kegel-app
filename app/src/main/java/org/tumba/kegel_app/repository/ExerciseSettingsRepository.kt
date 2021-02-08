package org.tumba.kegel_app.repository

import android.content.SharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tumba.kegel_app.core.system.Preference
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseSettingsRepository @Inject constructor(
    preferences: SharedPreferences
) {

    val lastCompletedExerciseDate = Preference<Long>(preferences, PREF_KEY_LAST_COMPLETED_EXERCISE_DATE, 0)

    val exercisesDurationInSeconds = Preference<Long>(preferences, PREF_EXERCISES_DURATION, DEFAULT_EXERCISES_DURATIONS)

    val exerciseDay = Preference<Int>(preferences, PREF_KEY_EXERCISE_DAY, DEFAULT_EXERCISE_DAY)

    val exerciseLevel = Preference<Int>(preferences, PREF_KEY_EXERCISE_LEVEL, DEFAULT_EXERCISE_LEVEL)

    val numberOfCompletedExercises = Preference<Int>(preferences, PREF_NUMBER_OF_EXERCISES, DEFAULT_NUMBER_OF_EXERCISES)

    val reminderDays = Preference<Int>(preferences, PREF_REMINDER_DAYS, DEFAULT_REMINDER_DAYS)

    val isReminderEnabled = Preference<Boolean>(preferences, PREF_REMINDER_ENABLED, DEFAULT_REMINDER_ENABLED)

    val reminderHour = Preference<Int>(preferences, PREF_KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)

    val reminderMinute = Preference<Int>(preferences, PREF_KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)

    val isVibrationEnabled = Preference<Boolean>(preferences, PREF_KEY_VIBRATION, DEFAULT_VIBRATION)

    val isNotificationEnabled = Preference<Boolean>(preferences, PREF_KEY_NOTIFICATION, DEFAULT_NOTIFICATION)

    companion object {
        private const val PREF_KEY_EXERCISE_LEVEL = "PREF_KEY_EXERCISE_LEVEL"
        private const val PREF_KEY_VIBRATION = "PREF_KEY_VIBRATION"
        private const val PREF_KEY_EXERCISE_DAY = "PREF_KEY_EXERCISE_DAY"
        private const val PREF_KEY_NOTIFICATION = "PREF_KEY_NOTIFICATION"
        private const val PREF_NUMBER_OF_EXERCISES = "PREF_NUMBER_OF_EXERCISES"
        private const val PREF_EXERCISES_DURATION = "PREF_EXERCISES_DURATION"
        private const val PREF_REMINDER_DAYS = "PREF_REMINDER_DAYS"
        private const val PREF_REMINDER_ENABLED = "PREF_REMINDER_ENABLED"
        private const val PREF_KEY_LAST_COMPLETED_EXERCISE_DATE = "PREF_KEY_LAST_COMPLETED_EXERCISE_DATE"
        private const val PREF_KEY_REMINDER_HOUR = "PREF_KEY_REMINDER_HOUR"
        private const val PREF_KEY_REMINDER_MINUTE = "PREF_KEY_REMINDER_MINUTE"
        private const val DEFAULT_EXERCISE_LEVEL = 1
        private const val DEFAULT_EXERCISE_DAY = 1
        private const val DEFAULT_VIBRATION = true
        private const val DEFAULT_NOTIFICATION = true
        private const val DEFAULT_NUMBER_OF_EXERCISES = 0
        private const val DEFAULT_EXERCISES_DURATIONS = 0L
        private const val DEFAULT_REMINDER_DAYS = 0x1111111
        private const val DEFAULT_REMINDER_ENABLED = true
        private const val DEFAULT_REMINDER_HOUR = 12
        private const val DEFAULT_REMINDER_MINUTE = 0
    }
}
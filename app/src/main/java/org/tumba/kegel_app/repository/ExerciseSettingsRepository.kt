package org.tumba.kegel_app.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tumba.kegel_app.core.system.Preference
import org.tumba.kegel_app.core.system.SharedPreferenceBooleanLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseSettingsRepository @Inject constructor(
    private val preferences: SharedPreferences
) {

    val lastCompletedExerciseDate: Preference<Long> = Preference(preferences, PREF_KEY_LAST_COMPLETED_EXERCISE_DATE, 0)
    val exercisesDurationInSeconds: Preference<Long> = Preference(preferences, PREF_EXERCISES_DURATION, DEFAULT_EXERCISES_DURATIONS)
    val exerciseDay: Preference<Int> = Preference(preferences, PREF_KEY_EXERCISE_DAY, DEFAULT_EXERCISE_DAY)
    val exerciseLevel: Preference<Int> = Preference(preferences, PREF_KEY_EXERCISE_LEVEL, DEFAULT_EXERCISE_LEVEL)
    val numberOfCompletedExercises: Preference<Int> =
        Preference(preferences, PREF_NUMBER_OF_EXERCISES, DEFAULT_NUMBER_OF_EXERCISES)


    fun setVibrationEnabled(enabled: Boolean) {
        setBool(PREF_KEY_VIBRATION, enabled)
    }

    fun observeVibrationEnabled(): LiveData<Boolean> {
        return SharedPreferenceBooleanLiveData(preferences, PREF_KEY_VIBRATION, DEFAULT_VIBRATION)
    }

    fun isVibrationEnabled(): Boolean {
        return preferences.getBoolean(PREF_KEY_VIBRATION, DEFAULT_VIBRATION)
    }

    fun isNotificationEnabled(): Boolean {
        return preferences.getBoolean(PREF_KEY_NOTIFICATION, DEFAULT_NOTIFICATION)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        setBool(PREF_KEY_NOTIFICATION, enabled)
    }

    fun observeNotificationEnabled(): LiveData<Boolean> {
        return SharedPreferenceBooleanLiveData(preferences, PREF_KEY_NOTIFICATION, DEFAULT_NOTIFICATION)
    }

    private fun setBool(key: String, value: Boolean) {
        preferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    companion object {
        private const val PREF_KEY_EXERCISE_LEVEL = "PREF_KEY_EXERCISE_LEVEL"
        private const val PREF_KEY_VIBRATION = "PREF_KEY_VIBRATION"
        private const val PREF_KEY_EXERCISE_DAY = "PREF_KEY_EXERCISE_DAY"
        private const val PREF_KEY_NOTIFICATION = "PREF_KEY_NOTIFICATION"
        private const val PREF_NUMBER_OF_EXERCISES = "PREF_NUMBER_OF_EXERCISES"
        private const val PREF_EXERCISES_DURATION = "PREF_EXERCISES_DURATION"
        private const val PREF_KEY_LAST_COMPLETED_EXERCISE_DATE = "PREF_KEY_LAST_COMPLETED_EXERCISE_DATE"
        private const val DEFAULT_EXERCISE_LEVEL = 1
        private const val DEFAULT_EXERCISE_DAY = 1
        private const val DEFAULT_VIBRATION = true
        private const val DEFAULT_NOTIFICATION = true
        private const val DEFAULT_NUMBER_OF_EXERCISES = 0
        private const val DEFAULT_EXERCISES_DURATIONS = 0L
    }
}
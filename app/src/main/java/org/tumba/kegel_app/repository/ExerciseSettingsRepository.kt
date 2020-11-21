package org.tumba.kegel_app.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.tumba.kegel_app.core.system.SharedPreferenceBooleanLiveData
import org.tumba.kegel_app.core.system.SharedPreferenceIntLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseSettingsRepository @Inject constructor(
    private val preferences: SharedPreferences
) {

    fun setExerciseLevel(level: Int) {
        setInt(PREF_KEY_EXERCISE_LEVEL, level)
    }

    fun observeExerciseLevel(): LiveData<Int> {
        return SharedPreferenceIntLiveData(
            preferences,
            PREF_KEY_EXERCISE_LEVEL,
            DEFAULT_EXERCISE_LEVEL
        )
    }

    fun getExerciseLevel(): Int {
        return preferences.getInt(PREF_KEY_EXERCISE_LEVEL, DEFAULT_EXERCISE_LEVEL)
    }

    fun setNumberOfCompletedExercises(exercised: Int) {
        setInt(PREF_KEY_EXERCISE_LEVEL, exercised)
    }

    fun getNumberOfCompletedExercises(): Int {
        return preferences.getInt(PREF_NUMBER_OF_EXERCISES, DEFAULT_NUMBER_OF_EXERCISES)
    }

    fun setExerciseDay(day: Int) {
        setInt(PREF_KEY_EXERCISE_LEVEL, day)
    }

    fun getExerciseDay(): LiveData<Int> {
        return SharedPreferenceIntLiveData(preferences, PREF_KEY_EXERCISE_DAY, DEFAULT_EXERCISE_DAY)
    }

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

    private fun setInt(key: String, value: Int) {
        preferences.edit()
            .putInt(key, value)
            .apply()
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
        private const val DEFAULT_EXERCISE_LEVEL = 1
        private const val DEFAULT_EXERCISE_DAY = 1
        private const val DEFAULT_VIBRATION = true
        private const val DEFAULT_NOTIFICATION = true
        private const val DEFAULT_NUMBER_OF_EXERCISES = 0
    }
}
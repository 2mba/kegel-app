package org.tumba.kegel_app.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.tumba.kegel_app.core.system.SharedPreferenceBooleanLiveData
import org.tumba.kegel_app.core.system.SharedPreferenceIntLiveData

class ExerciseSettingsRepository(
    private val preferences: SharedPreferences
) {

    companion object {
        private const val PREF_KEY_EXERCISE_LEVEL = "PREF_KEY_EXERCISE_LEVEL"
        private const val PREF_KEY_VIBRATION = "PREF_KEY_VIBRATION"
        private const val PREF_KEY_EXERCISE_DAY = "PREF_KEY_EXERCISE_DAY"
        private const val DEFAULT_EXERCISE_LEVEL = 1
        private const val DEFAULT_EXERCISE_DAY = 1
        private const val DEFAULT_VIBRATION = true
    }

    fun setExerciseLevel(level: Int) {
        setInt(PREF_KEY_EXERCISE_LEVEL, level)
    }

    fun getExerciseLevel(): LiveData<Int> {
        return SharedPreferenceIntLiveData(
            preferences,
            PREF_KEY_EXERCISE_LEVEL,
            DEFAULT_EXERCISE_LEVEL
        )
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

    fun isVibrationEnabled(): LiveData<Boolean> {
        return SharedPreferenceBooleanLiveData(preferences, PREF_KEY_VIBRATION, DEFAULT_VIBRATION)
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
}
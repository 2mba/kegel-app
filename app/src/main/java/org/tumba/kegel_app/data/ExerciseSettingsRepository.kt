package org.tumba.kegel_app.data

import io.reactivex.Completable
import io.reactivex.Observable
import org.tumba.kegel_app.core.system.IPreferences

class ExerciseSettingsRepository(
    private val preferences: IPreferences
) {

    companion object {
        private const val PREF_KEY_EXERCISE_LEVEL = "PREF_KEY_EXERCISE_LEVEL"
        private const val PREF_KEY_VIBRATION = "PREF_KEY_VIBRATION"
        private const val PREF_KEY_EXERCISE_DAY = "PREF_KEY_EXERCISE_DAY"
        private const val DEFAULT_EXERCISE_LEVEL = 1
        private const val DEFAULT_EXERCISE_DAY = 1
        private const val DEFAULT_VIBRATION = true
    }


    fun setExerciseLevel(level: Int): Completable {
        return Completable.fromAction {
            preferences.getInt(PREF_KEY_EXERCISE_LEVEL, DEFAULT_EXERCISE_LEVEL).set(level)
        }
    }

    fun getExerciseLevel(): Observable<Int> {
        return preferences.getInt(PREF_KEY_EXERCISE_LEVEL, DEFAULT_EXERCISE_LEVEL).asObservable()
    }

    fun setExerciseDay(day: Int): Completable {
        return Completable.fromAction {
            preferences.getInt(PREF_KEY_EXERCISE_DAY, DEFAULT_EXERCISE_DAY).set(day)
        }
    }

    fun getExerciseDay(): Observable<Int> {
        return preferences.getInt(PREF_KEY_EXERCISE_DAY, DEFAULT_EXERCISE_DAY).asObservable()
    }

    fun setVibrationEnabled(enabled: Boolean): Completable {
        return Completable.fromAction {
            preferences.getBoolean(PREF_KEY_VIBRATION, DEFAULT_VIBRATION).set(enabled)
        }
    }

    fun isVibrationEnabled(): Observable<Boolean> {
        return preferences.getBoolean(PREF_KEY_VIBRATION, DEFAULT_VIBRATION).asObservable()
    }
}
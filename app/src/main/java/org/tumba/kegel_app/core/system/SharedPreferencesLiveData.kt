package org.tumba.kegel_app.core.system


import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

abstract class SharedPreferenceLiveData<T>(
    val sharedPrefs: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int =
        sharedPrefs.getInt(key, defValue)
}

class SharedPreferenceStringLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: String
) :
    SharedPreferenceLiveData<String>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String): String =
        sharedPrefs.getString(key, defValue).orEmpty()
}

class SharedPreferenceBooleanLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Boolean
) :
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defValue)
}

class SharedPreferenceFloatLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Float) :
    SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Float): Float =
        sharedPrefs.getFloat(key, defValue)
}

class SharedPreferenceLongLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Long) :
    SharedPreferenceLiveData<Long>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Long): Long =
        sharedPrefs.getLong(key, defValue)
}

class SharedPreferenceStringSetLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Set<String>
) :
    SharedPreferenceLiveData<Set<String>>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>): Set<String> =
        sharedPrefs.getStringSet(key, defValue).orEmpty()
}

fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> {
    return SharedPreferenceIntLiveData(this, key, defValue)
}

fun SharedPreferences.stringLiveData(
    key: String,
    defValue: String
): SharedPreferenceLiveData<String> {
    return SharedPreferenceStringLiveData(this, key, defValue)
}

fun SharedPreferences.booleanLiveData(
    key: String,
    defValue: Boolean
): SharedPreferenceLiveData<Boolean> {
    return SharedPreferenceBooleanLiveData(this, key, defValue)
}

fun SharedPreferences.floatLiveData(key: String, defValue: Float): SharedPreferenceLiveData<Float> {
    return SharedPreferenceFloatLiveData(this, key, defValue)
}

fun SharedPreferences.longLiveData(key: String, defValue: Long): SharedPreferenceLiveData<Long> {
    return SharedPreferenceLongLiveData(this, key, defValue)
}

fun SharedPreferences.stringSetLiveData(
    key: String,
    defValue: Set<String>
): SharedPreferenceLiveData<Set<String>> {
    return SharedPreferenceStringSetLiveData(this, key, defValue)
}

@Suppress("FunctionName")
inline fun <reified T> Preference(
    preferences: SharedPreferences,
    key: String,
    defValue: T,
): Preference<T> {
    return PreferenceImpl(key, defValue, preferences, T::class.java)
}


interface Preference<T> {
    val key: String
    var value: T

    fun asFlow(): Flow<T>
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceImpl<T>(
    override val key: String,
    private val defValue: T,
    private val preferences: SharedPreferences,
    private val clazz: Class<T>
) : Preference<T> {

    private val flowPreference by lazy { FlowSharedPreferences(preferences) }

    override var value: T
        get() = loadValue()
        set(value) {
            setValue(value)
        }

    override fun asFlow(): Flow<T> {
        return when (clazz) {
            java.lang.Integer::class.java -> flowPreference.getInt(key, defValue as Int).asFlow()
            java.lang.Long::class.java -> flowPreference.getLong(key, defValue as Long).asFlow()
            java.lang.Boolean::class.java -> flowPreference.getBoolean(key, defValue as Boolean).asFlow()
            else -> throw IllegalStateException("Unknown class $clazz")
        } as Flow<T>
    }

    private fun loadValue(): T {
        return when (clazz) {
            java.lang.Integer::class.java -> preferences.getInt(key, defValue as Int)
            java.lang.Long::class.java -> preferences.getLong(key, defValue as Long)
            java.lang.Boolean::class.java -> preferences.getBoolean(key, defValue as Boolean)
            else -> throw IllegalStateException("Unknown class $clazz")
        } as T
    }

    private fun setValue(value: T): T {
        return when (clazz) {
            java.lang.Integer::class.java -> preferences.edit().putInt(key, value as Int).apply()
            java.lang.Long::class.java -> preferences.edit().putLong(key, value as Long).apply()
            java.lang.Boolean::class.java -> preferences.edit().putBoolean(key, value as Boolean).apply()
            else -> throw IllegalStateException("Unknown class $clazz")
        } as T
    }
}
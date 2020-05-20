package org.tumba.kegel_app.core.system

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable


interface IPreferences {

    fun getString(key: String, defaultValue: String): IPreference<String>

    fun getBoolean(key: String, defaultValue: Boolean): IPreference<Boolean>

    fun getInt(key: String, defaultValue: Int): IPreference<Int>
}

interface IPreference<T> {

    fun get(): T

    fun set(value: T)

    fun isSet(): Boolean

    fun delete()

    fun asObservable(): Observable<T>
}

class Preference<T>(private val preference: Preference<T>) : IPreference<T> {

    override fun get(): T = preference.get()

    override fun set(value: T) {
        preference.set(value)
    }

    override fun isSet(): Boolean = preference.isSet

    override fun delete() {
        preference.delete()
    }

    override fun asObservable(): Observable<T> = preference.asObservable()
}

class Preferences(preferences: SharedPreferences) : IPreferences {

    private var rxPreferences = RxSharedPreferences.create(preferences)

    override fun getString(key: String, defaultValue: String): IPreference<String> {
        return Preference(rxPreferences.getString(key, defaultValue))
    }

    override fun getBoolean(key: String, defaultValue: Boolean): IPreference<Boolean> {
        return Preference(rxPreferences.getBoolean(key, defaultValue))
    }

    override fun getInt(key: String, defaultValue: Int): IPreference<Int> {
        return Preference(rxPreferences.getInteger(key, defaultValue))
    }
}